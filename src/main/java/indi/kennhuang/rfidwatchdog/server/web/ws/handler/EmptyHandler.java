package indi.kennhuang.rfidwatchdog.server.web.ws.handler;

import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;

public class EmptyHandler implements WebSocketHandler {
    @Override
    public String getName() {
        return "Empty Handler";
    }

}
