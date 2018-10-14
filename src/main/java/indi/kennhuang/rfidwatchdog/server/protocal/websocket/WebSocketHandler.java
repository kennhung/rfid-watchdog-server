package indi.kennhuang.rfidwatchdog.server.protocal.websocket;

public interface WebSocketHandler {
    void route(int msgType, String msg);
}
