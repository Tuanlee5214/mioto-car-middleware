/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package cache;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tuanlee
 */
public class SimpleCacheTest {
    
     @Test
    public void hitThenMissAfterRemove() {
        SimpleCache<Integer, String> cache = new SimpleCache<Integer, String>("user");
        cache.put(1, "a");
        assertEquals("a", cache.get(1));
        assertEquals(1, cache.getHit());
        cache.remove(1);
        assertNull(cache.get(1));
        assertEquals(1, cache.getMiss());
    }

    @Test
    public void evictsTheLeastRecentlyUsed() {
        // temporarily point this test at a [SimpleCache@tiny] section with max-size = 3
        SimpleCache<Integer, String> cache = new SimpleCache<Integer, String>("sessiontest");
        cache.put(1, "a"); cache.put(2, "b"); cache.put(3, "c");
        assertNotNull(cache.get(1));          // touching 1 makes 2 the oldest
        cache.put(4, "d");                    // over capacity -> evict the LRU
        assertEquals(3, cache.size());
        assertNotNull(cache.get(1));          // survived because we touched it
        assertNull(cache.get(2));             // evicted
    }

    @Test
    public void entryExpires() throws Exception {
        // [SimpleCache@fast] with ttl-seconds = 1
        SimpleCache<Integer, String> cache = new SimpleCache<Integer, String>("usertest");
        cache.put(1, "a");
        assertEquals("a", cache.get(1));
        Thread.sleep(1200);
        assertNull("must expire after the TTL", cache.get(1));
        assertEquals(1, cache.getExpired());
    }
}
