package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestGroup {
    @BeforeClass
    public static void setup() throws SQLException {
        WatchDogLogger.init(true);
        SQLite.openDatabase("jdbc:sqlite:test.db");
    }

    @AfterClass
    public static void tearDown() {
        SQLite.closeDatabase();
    }

    @Test
    public void testCheck() throws SQLException {
        Group g = new Group();
        g.name = "TestG";
        Group.saveGroup(g);

        Group g2 = Group.findGroupByName(g.name);
        assert g2 != null;
        assertTrue(g2.name.equals(g.name));
    }
}
