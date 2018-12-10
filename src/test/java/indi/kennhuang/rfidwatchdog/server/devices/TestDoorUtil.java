package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.util.DoorUtil;
import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.protocal.hardware.enums.TypesEnum;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDoorUtil {

    @Test
    public void testCheck() {
        WatchDogLogger.init(true);
        SQLite.openDatabase("jdbc:sqlite:test.db");

        WatchDogLogger logger = new WatchDogLogger(LogType.Main, Level.OFF,Level.OFF);

        User user = new User();
        user.metadata = "{}";
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
        JSONObject out = DoorUtil.check(input, logger);

        System.out.println(out.toString());
        assertEquals(true,out.getBoolean("open"));
        SQLite.closeDatabase();
    }
}
