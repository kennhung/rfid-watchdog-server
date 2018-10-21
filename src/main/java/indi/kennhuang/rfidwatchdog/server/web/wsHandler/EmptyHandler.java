package indi.kennhuang.rfidwatchdog.server.web.wsHandler;

import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;

public class EmptyHandler implements WebSocketHandler {
    @Override
    public String getName() {
        return "Empty Handler";
    }

    @Override
    public void serve(String msgType, String msg) {

    }
}
