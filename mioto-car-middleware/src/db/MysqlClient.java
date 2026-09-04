/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import error.Err;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import org.apache.log4j.Logger;
import util.Config;

/**
 *
 * @author tuanlee
 */
public class MysqlClient {
    private static final Logger _Logger = Logger.getLogger(MysqlClient.class);

    /**
     * Called once per returned row, while the connection is still open.
     */
    public interface IRowListener {

        void onRow(ResultSet rs) throws SQLException;
    }

    private final SimpleConnectionPool _pool;
    private final int _nretry;
    private final boolean _logQuery;

    public MysqlClient(String name) {
        _pool = new SimpleConnectionPool(name);
        _nretry = Config.getInt(MysqlClient.class, name, "nretry", 2);
        _logQuery = Config.getBoolean(MysqlClient.class, name, "log-query", false);
    }

    public SimpleConnectionPool getPool() {
        return _pool;
    }

    /**
     * SELECT. Returns SUCCESS, NOT_EXIST when there are no rows, or a failure
     * code.
     */
    public int executeQuery(IRowListener listener, String query, Object... params) {
        for (int retry = 0; retry < _nretry; ++retry) {
            Connection conn = _pool.borrow();
            if (conn == null) {
                return Err.NO_CONNECTION;
            }
            boolean ok = true;
            try {
                PreparedStatement prst = conn.prepareStatement(query);
                try {
                    bind(prst, params);
                    if (_logQuery) {
                        _Logger.info("query: " + prst.toString());
                    }
                    ResultSet rs = prst.executeQuery();
                    try {
                        if (!rs.next()) {
                            return Err.NOT_EXIST;      // zero rows is a CODE, not an empty list
                        }
                        if (listener != null) {
                            do {
                                listener.onRow(rs);    // do/while: the first row is already read
                            } while (rs.next());
                        }
                        return Err.SUCCESS;
                    } finally {
                        rs.close();
                    }
                } finally {
                    prst.close();
                }
            } catch (SQLException ex) {
                ok = false;                            // this connection is suspect
                _Logger.error("query failed (retry " + retry + "): " + query
                        + " params=" + Arrays.toString(params), ex);
            } finally {
                _pool.giveBack(conn, ok);              // ALWAYS - including the returns above
            }
        }
        return Err.BAD_CONNECTION;                     // retries exhausted
    }

    /**
     * INSERT / UPDATE / DELETE. Returns the affected row count, or a negative
     * error code.
     */
    public int executeUpdate(String query, Object... params) {
        for (int retry = 0; retry < _nretry; ++retry) {
            Connection conn = _pool.borrow();
            if (conn == null) {
                return Err.NO_CONNECTION;
            }
            boolean ok = true;
            try {
                PreparedStatement prst = conn.prepareStatement(query);
                try {
                    bind(prst, params);
                    if (_logQuery) {
                        _Logger.info("update: " + prst.toString());
                    }
                    return prst.executeUpdate();
                } finally {
                    prst.close();
                }
            } catch (SQLException ex) {
                ok = false;
                _Logger.error("update failed (retry " + retry + "): " + query, ex);
            } finally {
                _pool.giveBack(conn, ok);
            }
        }
        return Err.BAD_CONNECTION;
    }

    /**
     * INSERT returning the new AUTO_INCREMENT id, or a negative error code.
     */
    public long executeInsertAndReturnKey(String query, Object... params) {
        for (int retry = 0; retry < _nretry; ++retry) {
            Connection conn = _pool.borrow();
            if (conn == null) {
                return Err.NO_CONNECTION;
            }
            boolean ok = true;
            try {
                PreparedStatement prst
                        = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                try {
                    bind(prst, params);
                    if (_logQuery) {
                        _Logger.info("insert: " + prst.toString());
                    }
                    if (prst.executeUpdate() != 1) {
                        return Err.FAIL;
                    }
                    ResultSet keys = prst.getGeneratedKeys();
                    try {
                        if (keys.next()) {
                            return keys.getLong(1);
                        }
                        return Err.FAIL;
                    } finally {
                        keys.close();
                    }
                } finally {
                    prst.close();
                }
            } catch (SQLException ex) {
                ok = false;
                _Logger.error("insert failed (retry " + retry + "): " + query, ex);
            } finally {
                _pool.giveBack(conn, ok);
            }
        }
        return Err.BAD_CONNECTION;
    }

    private void bind(PreparedStatement prst, Object[] params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.length; ++i) {
            prst.setObject(i + 1, params[i]);     // JDBC parameters are 1-based
        }
    }
}
