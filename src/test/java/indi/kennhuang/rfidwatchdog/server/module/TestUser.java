package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestUser {
    @Test
    public void testCheck() {
        SQLite.openDatabase("jdbc:sqlite:test.db");

        User user = new User();
        user.metadata = "{}";
        user.doors.add(new DoorPermission(2, true));
        user.name = "TestUser";
        user.uid = "107AC648";
        user.metadata = "{\"test\":true}";
        //Create user

        User userRes;
        try {
            User.saveUser(user);
            userRes = User.getUserByUid(user.uid);
            assertTrue(userRes.name.equals(user.name));
            user.id = userRes.id;
            user.name = "TestUser2";

            //Test 2
            User.saveUser(user);
            userRes = User.getUserById(user.id);
            assertTrue(userRes.name.equals(user.name));


            User.deleteUser(user);
            userRes = User.getUserById(user.id);
            assertSame(null,userRes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
