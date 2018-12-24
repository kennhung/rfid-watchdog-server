package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.module.Group;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class GroupsHandler implements WebSocketHandler {
    private WebSocketServer.WatchdogWebSocket ws;

    public GroupsHandler(WebSocketServer.WatchdogWebSocket watchdogWebSocket) {
        ws = watchdogWebSocket;
    }

    @Override
    public String getName() {
        return "Groups";
    }

    public void getGroups(String data) {
        try {
            List groups = Group.getAllGroups();
            JSONArray groupsOut = new JSONArray();
            if(groups != null) {
                Iterator usersIterator = groups.iterator();
                while (usersIterator.hasNext()) {
                    Group group = (Group) usersIterator.next();
                    groupsOut.put(Group.decodeGroup(group));
                }
            }
            ws.send("groupsList",groupsOut.toString());
        } catch (SQLException e) {
            ws.sendErr(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGroup(String data) {
        JSONObject editGroup = new JSONObject(data);
        try {
            Group group = Group.encodeGroup(editGroup);
            Group.saveGroup(group);
        } catch (SQLException| JSONException e) {
            ws.sendErr(e.getMessage());
        }
    }

    public void deleteGroup(String data){
        int id = Integer.parseInt(data);
        try {
            Group g = Group.findGroupById(id);
            if(g == null){
                ws.sendErr("User not found");
            }
            else{
                Group.deleteGroup(g);
            }
        } catch (SQLException e) {
            ws.sendErr(e.getMessage());
        }
    }

}
