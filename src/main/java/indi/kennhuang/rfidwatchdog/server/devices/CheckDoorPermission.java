package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.module.DoorPermission;
import indi.kennhuang.rfidwatchdog.server.module.Group;
import indi.kennhuang.rfidwatchdog.server.module.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class CheckDoorPermission {

    public static JSONObject check(JSONObject in) {
        JSONObject reply = new JSONObject();
        try {
            User resUser = User.getUserByUid(in.getString("uid"));
            if (resUser == null) {
                reply.put("open", false);
                reply.put("name", "Unknown");
            } else {
                if (checkDoorsAccessibility(resUser.doors, in.getInt("doorId"))) {
                    reply.put("open", true);
                } else {
                    if (checkGroupDoorsAccessibility(new JSONArray(resUser.groups), in.getInt("doorId"))) {
                        reply.put("open", true);
                    } else {
                        reply.put("open", false);
                    }
                }
                reply.put("name", resUser.name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reply;
    }


    private static boolean checkGroupDoorsAccessibility(JSONArray groups, int doorId) {
        for (int i = 0; i < groups.length(); i++) {
            try {
                Group group = Group.findGroupById(groups.getInt(i));
                if (group == null) {
                    continue;
                }
                if (checkDoorsAccessibility(group.doors, doorId)) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Access Denied
        return false;
    }


    private static boolean checkDoorsAccessibility(List<DoorPermission> doorsList, int doorId) {
        DoorPermission[] doors = new DoorPermission[doorsList.size()];
        doors = doorsList.toArray(doors);
        for (int i = 0; i < doors.length; i++) {
            DoorPermission door = doors[i];
            if (door.doorId == doorId && (door.validDate > System.currentTimeMillis() / 1000L) && door.open) {
                //Access Granted
                return true;
            }
        }
        //Access Denied
        return false;
    }
}
