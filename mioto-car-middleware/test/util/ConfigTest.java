/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package util;

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
public class ConfigTest {
    public static class DummyThriftServer {}
    public static class DummySimpleConnectionPool {}
    public static class DummyMysqlClient {}
    
    
    @Test
    public void readsValuesFromSection() {
        assertEquals(10100, Config.getInt(DummyThriftServer.class, "mioto", "port", 0));
        assertEquals("mioto_lab",
                Config.getString(DummySimpleConnectionPool.class, "mioto", "dbname", ""));
        assertTrue(Config.getBoolean(DummyMysqlClient.class, "mioto", "log-query", false));
    }

    @Test
    public void fallsBackWhenKeyOrSectionMissing() {
        assertEquals(4,  Config.getInt(DummySimpleConnectionPool.class, "mioto", "max-conn", 4));
    }
}
