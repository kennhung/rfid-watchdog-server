package indi.kennhuang.rfidwatchdog.server.protocal.websocket;

public interface WebSocketHandler {
    String getName();
    void route(int msgType, String msg);
}
