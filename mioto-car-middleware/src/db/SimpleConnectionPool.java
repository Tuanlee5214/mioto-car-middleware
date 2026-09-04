/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import util.Config;

/**
 *
 * @author tuanlee
 */
public class SimpleConnectionPool {
    // ---- immutable config ----
    private final String _driver, _url, _user, _pwd, _dbName;
    private final int _maxConn;
    private final int _connTimeoutMs;      // socket/login timeout for opening
    private final int _waitTimeoutMs;      // how long a borrower waits for a free slot

    // ---- mutable state: ALL of it guarded by _lock ----
    private final Queue<Connection> _idle = new LinkedList<>();
    private int _total = 0;                // idle + checked out + opens in flight
    private final Object _lock = new Object();

    // ---- counters: read without the lock, so they must be atomic ----
    private final AtomicLong _created  = new AtomicLong();
    private final AtomicLong _failed   = new AtomicLong();
    private final AtomicLong _borrowed = new AtomicLong();
    private final AtomicLong _timeouts = new AtomicLong();
    private final AtomicLong _discarded = new AtomicLong();

    public SimpleConnectionPool(String name) {
        _driver = Config.getString(SimpleConnectionPool.class, name, "driver", "com.mysql.cj.jdbc.Driver");
        String host = Config.getString(SimpleConnectionPool.class, name, "host", "127.0.0.1:3306");
        _dbName = Config.getString(SimpleConnectionPool.class, name, "dbname", "");
        _user   = Config.getString(SimpleConnectionPool.class, name, "uname", "");
        _pwd    = Config.getString(SimpleConnectionPool.class, name, "pwd", "");

        _maxConn       = Config.getInt(SimpleConnectionPool.class, name, "max_conn", 4);
        _connTimeoutMs = Config.getInt(SimpleConnectionPool.class, name, "conn-timeout", 5000);
        _waitTimeoutMs = Config.getInt(SimpleConnectionPool.class, name, "wait-timeout", 3000);

        _url = "jdbc:mysql://" + host + "/" + _dbName
             + "?useUnicode=true&characterEncoding=UTF-8"
             + "&connectTimeout=" + _connTimeoutMs
             + "&socketTimeout=" + (_connTimeoutMs * 4);

        try {
            Class.forName(_driver);        // once, here, not on every open
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("JDBC driver not on the classpath: " + _driver, ex);
        }
    }

    /**
     * Take a connection out of the pool, or open a new one if we are under the cap.
     * Returns null when no connection could be obtained (caller returns an error code).
     */
    public Connection borrow() {
        final long deadline = System.currentTimeMillis() + _waitTimeoutMs;

        synchronized (_lock) {
            for (;;) {
                // (a) reuse an idle connection, discarding any that died while idle
                while (!_idle.isEmpty()) {
                    Connection conn = _idle.poll();
                    if (isAlive(conn)) {
                        _borrowed.incrementAndGet();
                        return conn;
                    }
                    closeQuietly(conn);      // dead: close it and stop counting it
                    _total--;
                    _discarded.incrementAndGet();
                }

                // (b) nothing idle: claim a slot if we are under the cap
                if (_total < _maxConn) {
                    _total++;                // claim BEFORE opening - see 6.3
                    break;
                }

                // (c) at capacity: wait, with a deadline
                long remain = deadline - System.currentTimeMillis();
                if (remain <= 0) {
                    _timeouts.incrementAndGet();
                    return null;
                }
                try {
                    _lock.wait(remain);      // releases the monitor; woken by giveBack()
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();   // restore the flag, always
                    return null;
                }
            }
        }

        // (d) open OUTSIDE the lock - this does network I/O and can take seconds
        try {
            Connection conn = open();
            _created.incrementAndGet();
            _borrowed.incrementAndGet();
            return conn;
        } catch (SQLException ex) {
            synchronized (_lock) {
                _total--;                    // give the claimed slot back
                _lock.notifyAll();           // ... and let a waiter try
            }
            _failed.incrementAndGet();
            return null;
        }
    }

    /**
     * Return a connection. Pass ok=false if the caller saw a SQLException, so the
     * suspect connection is thrown away instead of handed to the next borrower.
     */
    public void giveBack(Connection conn, boolean ok) {
        if (conn == null) {
            return;
        }
        synchronized (_lock) {
            if (ok) {
                _idle.add(conn);
            } else {
                closeQuietly(conn);
                _total--;
                _discarded.incrementAndGet();
            }
            _lock.notifyAll();               // a slot is free: wake a waiting borrower
        }
    }

    private Connection open() throws SQLException {
        Connection conn = DriverManager.getConnection(_url, _user, _pwd);
        // per-session charset, so Vietnamese text and emoji survive
        PreparedStatement prst = conn.prepareStatement("SET NAMES 'utf8mb4'");
        try {
            prst.execute();
        } finally {
            prst.close();
        }
        return conn;
    }

    /** Bounded liveness check - isValid(0) means "no timeout" and can hang under the lock. */
    private boolean isAlive(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(1);
        } catch (SQLException ex) {
            return false;
        }
    }

    private void closeQuietly(Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
        }
    }

    /** Close everything. Called from the shutdown hook. */
    public void shutdown() {
        synchronized (_lock) {
            while (!_idle.isEmpty()) {
                closeQuietly(_idle.poll());
                _total--;
            }
        }
    }

    // ---- observability: a pool you cannot see into is a pool you cannot debug ----
    public int  getTotal()      { synchronized (_lock) { return _total; } }
    public int  getIdle()       { synchronized (_lock) { return _idle.size(); } }
    public int  getBusy()       { synchronized (_lock) { return _total - _idle.size(); } }
    public long getCreated()    { return _created.get(); }
    public long getFailed()     { return _failed.get(); }
    public long getBorrowed()   { return _borrowed.get(); }
    public long getTimeouts()   { return _timeouts.get(); }
    public long getDiscarded()  { return _discarded.get(); }

    public String stats() {
        return "pool{total=" + getTotal() + ", idle=" + getIdle() + ", busy=" + getBusy()
             + ", created=" + getCreated() + ", failed=" + getFailed()
             + ", borrowed=" + getBorrowed() + ", timeouts=" + getTimeouts()
             + ", discarded=" + getDiscarded() + "}";
    }
}
