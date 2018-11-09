package indi.kennhuang.rfidwatchdog.server.module;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DoorPermission {
    public int doorId;
    public long validDate;
    public boolean open;

    public DoorPermission(int id, boolean open, long validDate) {
        doorId = id;
        this.open = open;
        this.validDate = validDate;
    }

    public DoorPermission(int id, boolean open) {
        doorId = id;
        this.open = open;
        this.validDate = System.currentTimeMillis() + (31536000 * 10) / 1000L;
    }

    public JSONObject getJSONObject() {
        return decodeDoorPermission(this);
    }

    public static DoorPermission encodeDoorPermission(JSONObject doorJson) {
        int doorId = doorJson.getInt("doorId");
        boolean open = doorJson.getBoolean("open");
        long validDate = doorJson.getLong("validDate");

        DoorPermission door = new DoorPermission(doorId, open, validDate);
        return door;
    }

    public static JSONObject decodeDoorPermission(DoorPermission doorPermission) {
        JSONObject json = new JSONObject();
        json.put("doorId", doorPermission.doorId);
        json.put("open", doorPermission.open);
        json.put("validDate", doorPermission.validDate);
        return json;
    }

    // DoorPermissions use String and List<DoorPermission>
    public static List<DoorPermission> encodeDoorPermissions(String in) {
        List<DoorPermission> doors = new ArrayList<DoorPermission>();
        JSONArray doorsJSON = new JSONArray(in);
        for (Object o : doorsJSON) {
            if (o instanceof JSONObject) {
                doors.add(DoorPermission.encodeDoorPermission((JSONObject) o));
            }
        }
        return doors;
    }

    public static String decodeDoorPermissions(List<DoorPermission> in) {
        Iterator i = in.iterator();
        JSONArray doors = new JSONArray();
        while (i.hasNext()){
            DoorPermission door = (DoorPermission) i.next();
            doors.put(door.getJSONObject());
        }
        return doors.toString();
    }
}
