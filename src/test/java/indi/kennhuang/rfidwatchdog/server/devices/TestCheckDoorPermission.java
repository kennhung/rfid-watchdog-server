package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.CheckDoorPermission;
import indi.kennhuang.rfidwatchdog.server.protocal.enums.TypesEnum;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCheckDoorPermission {

    @Test
    public void testCheck() {
        SQLite.openDatabase("jdbc:sqlite:test.db");

        JSONObject input = new JSONObject();
        input.put("type", TypesEnum.CARD_CHECK);
        input.put("doorId", 1);
        input.put("uid", "107FC648");
        JSONObject out = CheckDoorPermission.check(input);

        System.out.println(out.toString());
        assertEquals(out.get("open"),true);
    }
}
