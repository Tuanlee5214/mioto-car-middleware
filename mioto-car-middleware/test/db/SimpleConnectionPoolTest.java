/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tuanlee
 */
public class SimpleConnectionPoolTest {
    
    @Test
    public void borrowAndGiveBackReusesTheSameConnection() {
        SimpleConnectionPool pool = new SimpleConnectionPool("mioto");
        Connection a = pool.borrow();
        assertNotNull(a);
        pool.giveBack(a, true);
        Connection b = pool.borrow();
        assertSame("an idle connection must be reused, not reopened", a, b);
        pool.giveBack(b, true);
        assertEquals(1, pool.getCreated());
    }

    @Test
    public void neverExceedsTheCap() throws Exception {
        final SimpleConnectionPool pool = new SimpleConnectionPool("mioto"); // max-conn = 4
        final int threads = 30;
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done  = new CountDownLatch(threads);
        final AtomicInteger maxSeen = new AtomicInteger();
        ExecutorService ex = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; ++i) {
            ex.submit(new Runnable() {
                public void run() {
                    try {
                        start.await();                       // all at once
                        Connection c = pool.borrow();
                        if (c != null) {
                            int t = pool.getTotal();
                            while (t > maxSeen.get()) { maxSeen.set(t); t = pool.getTotal(); }
                            Thread.sleep(20);
                            pool.giveBack(c, true);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                }
            });
        }
        start.countDown();
        done.await(30, TimeUnit.SECONDS);
        ex.shutdown();

        assertTrue("cap breached: " + maxSeen.get(), maxSeen.get() <= 4);
        assertEquals("every connection must have been returned", 0, pool.getBusy());
    }

    @Test
    public void timesOutInsteadOfHangingWhenExhausted() {
        SimpleConnectionPool pool = new SimpleConnectionPool("mioto");   // max 4, wait 3000ms
        List<Connection> held = new ArrayList<Connection>();
        for (int i = 0; i < 4; ++i) {
            held.add(pool.borrow());
        }
        long t0 = System.currentTimeMillis();
        Connection fifth = pool.borrow();
        long elapsed = System.currentTimeMillis() - t0;

        assertNull("the 5th borrow must fail, not succeed", fifth);
        assertTrue("must wait about wait-timeout, was " + elapsed, elapsed >= 2900);
        assertEquals(1, pool.getTimeouts());

        for (Connection c : held) { pool.giveBack(c, true); }
    }

    @Test
    public void brokenConnectionIsDiscardedNotReused() {
        SimpleConnectionPool pool = new SimpleConnectionPool("mioto");
        Connection a = pool.borrow();
        pool.giveBack(a, false);                 // caller reports failure
        assertEquals(0, pool.getIdle());
        assertEquals(0, pool.getTotal());
        assertEquals(1, pool.getDiscarded());
    }
}
