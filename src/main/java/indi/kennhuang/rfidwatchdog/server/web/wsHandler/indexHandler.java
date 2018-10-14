package indi.kennhuang.rfidwatchdog.server.web.wsHandler;

import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.util.SystemInfo;
import indi.kennhuang.rfidwatchdog.server.web.WebSocketServer;
import org.json.JSONObject;

import java.io.IOException;

public class indexHandler implements WebSocketHandler {

    static final int fetchInfoOnLoad = 1;
    private WebSocketServer.WatchdogWebSocket ws;

    public indexHandler(WebSocketServer.WatchdogWebSocket ws){
        this.ws = ws;
    }

    @Override
    public String getName() {
        return "index";
    }

    @Override
    public void route(int msgType, String msg) {
        switch (msgType) {
            case fetchInfoOnLoad:
                JSONObject sendJson = new JSONObject();
                sendJson.put("type",fetchInfoOnLoad);
                JSONObject content = new JSONObject();
                content.put("uptime", SystemInfo.getSystemUptime().abs().getSeconds());
                sendJson.put("message",content.toString());
                try {
                    ws.send(sendJson.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
