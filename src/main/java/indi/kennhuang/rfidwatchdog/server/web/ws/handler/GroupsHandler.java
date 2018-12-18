package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.WatchdogServer;
import indi.kennhuang.rfidwatchdog.server.module.Group;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class GroupsHandler implements WebSocketHandler {
    WebSocketServer.WatchdogWebSocket ws;

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

}
