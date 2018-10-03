package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.util.DoorUtil;
import indi.kennhuang.rfidwatchdog.server.module.DoorPermission;
import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.protocal.enums.TypesEnum;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDoorUtil {

    @Test
    public void testCheck() {
        WatchDogLogger.init();
        SQLite.openDatabase("jdbc:sqlite:test.db");

        User user = new User();
        user.metadata = "{}";
        user.doors.add(new DoorPermission(2, true));
        user.name = "TestUser";
        user.uid = "TESTUID";
        try {
            User.saveUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Create user

        JSONObject input = new JSONObject();
        input.put("type", TypesEnum.decode(TypesEnum.types.CARD_CHECK));
        input.put("doorId", 2);
        input.put("uid", "TESTUID");
        JSONObject out = DoorUtil.check(input);

        System.out.println(out.toString());
        assertEquals(true,out.getBoolean("open"));
        SQLite.closeDatabase();
    }
}
