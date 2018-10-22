package indi.kennhuang.rfidwatchdog.server.web.wsHandler;

import indi.kennhuang.rfidwatchdog.server.module.User;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;

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
            List users = User.getAllUsers();
            Iterator u = users.iterator();
            while (u.hasNext()){
                User user = (User) u.next();
                System.out.println(user.name+" "+user.uid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
