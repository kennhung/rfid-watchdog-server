package indi.kennhuang.rfidwatchdog.server.web.wsHandler;

import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.system.SystemInfo;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONObject;

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

    @Override
    public void serve(String msgType, String data) {
        try {
            this.getClass().getMethod(msgType, String.class).invoke(this, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            try {
                ws.sendInternalError("Can't fine method of "+msgType);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void getBasicInfo(String data) {
        try {
            ws.send("UpdateBasicInfo", SystemInfo.getInfoInJson().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
