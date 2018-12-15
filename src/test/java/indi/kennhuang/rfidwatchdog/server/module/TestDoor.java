package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.module.permission.Permission;
import indi.kennhuang.rfidwatchdog.server.module.permission.PermissionBlock;
import indi.kennhuang.rfidwatchdog.server.util.Time;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.TestCase.*;

public class TestDoor {
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
        Door d = new Door();
        d.name = "TestDoor";
        d.auth_token = "TestAuthToken";

        PermissionBlock pb = new PermissionBlock();
        pb.permission = new Permission();
        pb.targetId = 1;
        pb.validate = Time.getUNIXTimeStamp();

        pb.permission.open = true;
        pb.permission.admin = false;

        d.permissionBlocks.put(1,pb);

        Door.saveDoor(d);

        Door dd = Door.findDoorByName(d.name);
        assert dd!=null;
        assertTrue(dd.auth_token.equals(d.auth_token));

        PermissionBlock gpb = dd.permissionBlocks.get(1);
        assertEquals(pb.validate,gpb.validate);
        assertEquals(pb.permission.open,gpb.permission.open);
        assertEquals(pb.permission.admin,gpb.permission.admin);

        Door.deleteDoor(d);
    }
}
