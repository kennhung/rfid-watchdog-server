package indi.kennhuang.rfidwatchdog.server.protocal.websocket;

public interface WebSocketHandler {
    String getName();
    void serve(String msgType, String msg);
}
