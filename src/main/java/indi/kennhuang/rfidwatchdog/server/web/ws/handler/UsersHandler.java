package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
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

    public void getUsers(String data) {
        try {
            JSONArray usersOut = new JSONArray();
            List users = User.getAllUsers();
            if (users != null) {
                Iterator usersIterator = users.iterator();
                while (usersIterator.hasNext()) {
                    User user = (User) usersIterator.next();
                    usersOut.put(User.decodeUser(user));
                }
            }
            ws.send("usersList", usersOut.toString());
        } catch (SQLException e) {
            sendErr(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getGroups(String data) {
        new GroupsHandler(ws).getGroups(data);
    }

    public void saveUser(String data) {
        JSONObject editUser = new JSONObject(data);
        try {
            User user = User.encodeUser(editUser);
            User.saveUser(user);
        } catch (SQLException| JSONException e) {
            sendErr(e.getMessage());
        }
    }

    public void deleteUser(String data){
        int id = Integer.parseInt(data);
        try {
            User u = User.getUserById(id);
            if(u == null){
                sendErr("User not found");
            }
            else{
                User.deleteUser(u);
            }
        } catch (SQLException e) {
            sendErr(e.getMessage());
        }
    }

    private void sendErr(String msg) {
        try {
            ws.sendInternalError(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
