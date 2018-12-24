package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.module.Door;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class DoorsHandler implements WebSocketHandler {
    WebSocketServer.WatchdogWebSocket ws;

    public  DoorsHandler(WebSocketServer.WatchdogWebSocket ws){
        this.ws = ws;
    }

    @Override
    public String getName() {
        return "Doors";
    }

    public void getDoors(String data) {
        try {
            List doors = Door.getAllDoors();
            JSONArray groupsOut = new JSONArray();
            if (doors != null) {
                Iterator usersIterator = doors.iterator();
                while (usersIterator.hasNext()) {
                    Door group = (Door) usersIterator.next();
                    groupsOut.put(Door.decodeDoor(group));
                }
            }
            ws.send("doorsList", groupsOut.toString());
        } catch (SQLException e) {
            ws.sendErr(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDoor(String data) {
        JSONObject editDoor = new JSONObject(data);
        try {
            Door door = Door.encodeDoor(editDoor);
            Door.saveDoor(door);
        } catch (SQLException | JSONException e) {
            ws.sendErr(e.getMessage());
        }
    }

    public void deleteDoor(String data) {
        int id = Integer.parseInt(data);
        try {
            Door d = Door.findDoorById(id);
            if (d == null) {
                ws.sendErr("User not found");
            } else {
                Door.deleteDoor(d);
            }
        } catch (SQLException e) {
            ws.sendErr(e.getMessage());
        }
    }

    public void updatePermissionBlocks(String data){
        JSONObject in = new JSONObject(data);
        int targetDoorId = in.getInt("target");

        try {
            Door d = Door.findDoorById(targetDoorId);
            if(d == null){
                ws.sendErr("Door id "+targetDoorId+" not found.");
                return;
            }
            d.permissionBlocks = Door.encodePermissionBlocks(in.getJSONArray("permission_blocks"));

            Door.saveDoor(d);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getGroups(String data){
        new GroupsHandler(ws).getGroups(data);
    }
}
