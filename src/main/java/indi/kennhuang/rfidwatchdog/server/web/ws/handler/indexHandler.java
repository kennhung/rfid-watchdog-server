package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.system.SystemInfo;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class indexHandler implements WebSocketHandler {

    private WebSocketServer.WatchdogWebSocket ws;

    public indexHandler(WebSocketServer.WatchdogWebSocket ws) {
        this.ws = ws;
    }

    @Override
    public String getName() {
        return "index";
    }

    public void getBasicInfo(String data) {
        try {
            ws.send("UpdateBasicInfo", SystemInfo.getInfoInJson().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
