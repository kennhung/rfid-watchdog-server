package indi.kennhuang.rfidwatchdog.server.web.wsHandler;

import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class UsersHandler implements WebSocketHandler {

    private WebSocketServer.WatchdogWebSocket ws;
    public UsersHandler(WebSocketServer.WatchdogWebSocket ws) {
        this.ws = ws;
    }

    @Override
    public String getName() {
        return "Users";
    }

    public void getUsers(String data){
        try {
            JSONArray usersOut = new JSONArray();
            List users = User.getAllUsers();
            Iterator usersIterator = users.iterator();
            while (usersIterator.hasNext()){
                User user = (User) usersIterator.next();
                JSONObject userOut = new JSONObject();
                userOut.put("name",user.name);
                userOut.put("uid",user.uid);
                userOut.put("id",user.id);
                userOut.put("metadata",user.metadata);
                userOut.put("groups",user.groups);
                userOut.put("door",user.getDoorsString());
                usersOut.put(userOut);
            }
            ws.send("usersList",usersOut.toString());
        } catch (SQLException e) {
            try {
                ws.sendInternalError(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
