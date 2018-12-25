package indi.kennhuang.rfidwatchdog.server.devices.util;

import indi.kennhuang.rfidwatchdog.server.module.Door;
import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.module.permission.PermissionBlock;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

public class DoorUtil {

    public static JSONObject check(JSONObject in, WatchDogLogger logger) {
        JSONObject reply = new JSONObject();
        try {
            User resUser = User.getUserByUid(in.getString("uid"));
            Door door = Door.findDoorById(in.getInt("doorId"));
            if (resUser == null) {
                reply.put("open", false);
                reply.put("name", "Unknown");
            } else if(door == null){
                reply.put("open", false);
                reply.put("name", "Unknown Door");
                logger.warning("Error while finding door with id="+in.getInt("doorId"));
            }
            else {
                if (checkDoorAccessibility(resUser, door)){
                    reply.put("open", true);
                } else {
                    reply.put("open", false);
                }
                reply.put("name", resUser.name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reply;
    }

    private static boolean checkDoorAccessibility(User user, Door door){
        JSONArray groups = new JSONArray(user.groups);
        for(int i=0;i<groups.length();i++){
            PermissionBlock pb = door.permissionBlocks.get(groups.getInt(i));
            if(pb.permission.open){
                return true;
            }
        }
        return false;
    }

}
