package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.util.Time;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestUser {

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
    public void testCheck() {
        User user = new User();
        user.metadata = "{}";
        user.name = "TestUser";
        user.uid = "TESTUID";
        user.metadata = "{\"test\":true}";
        user.password = "password";
        user.validate = Time.getUNIXTimeStamp();
        //Create user

        User userRes;
        try {
            User.saveUser(user);
            userRes = User.getUserByUid(user.uid);
            assert userRes != null;
            assertTrue(userRes.name.equals(user.name));
            assertEquals(user.validate, userRes.validate);
            user.id = userRes.id;
            user.name = "TestUser2";

            //Test 2
            User.saveUser(user);
            userRes = User.getUserById(user.id);
            assertTrue(userRes.name.equals(user.name));
            assertEquals(user.password, userRes.password);

            User.deleteUser(user);
            userRes = User.getUserById(user.id);
            assertSame(null,userRes);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
