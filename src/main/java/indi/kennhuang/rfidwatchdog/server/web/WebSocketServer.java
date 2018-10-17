package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoWSD;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.wsHandler.indexHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;

public class WebSocketServer extends NanoWSD {

    private WatchDogLogger logger = null;

    public WebSocketServer(int port, WatchDogLogger logger) {
        super(port);
        this.logger = logger;
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new WatchdogWebSocket(handshake, logger);
    }

    public static class WatchdogWebSocket extends WebSocket {
        private String uri;
        private WatchDogLogger logger;
        private WebSocketHandler handler;

        public WatchdogWebSocket(IHTTPSession handshakeRequest, WatchDogLogger logger) {
            super(handshakeRequest);
            this.logger = logger;
            uri = handshakeRequest.getUri();
            logger.debug("WS Connect '"+uri+"'");
            //TODO serve

            if(uri.equals("/")||uri.equals("/index")){
                handler = new indexHandler(this);
            }
            else {
                handler = null;
            }
            logger.debug("serving with handler: "+handler.getName());

            new Thread(() -> {
                while (true) {
                    try {
                        ping("Ping".getBytes());
                        Thread.sleep(4000);
                    } catch (IOException e) {
                        if (e.getMessage().equals("Socket closed")) {
                            break;
                        } else {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected void onOpen() {

        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode closeCode, String reason, boolean initiatedByRemote) {
            logger.debug("C [" + (initiatedByRemote ? "Remote" : "Self") + "] " + (closeCode != null ? closeCode : "UnknownCloseCode[" + closeCode + "]")
                    + (reason != null && !reason.isEmpty() ? ": " + reason : ""));
        }

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            logger.debug("M " + webSocketFrame);
            String receive = webSocketFrame.getTextPayload();
            JSONObject recJson = null;
            try {
                recJson = new JSONObject(receive);
                handler.serve(recJson.getString("type"),recJson.getString("data"));
            }
            catch(Exception e){
                try {
                    sendInternalError(e.getMessage());
                } catch (IOException IOE) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPong(WebSocketFrame webSocketFrame) {
            logger.finest("P " + webSocketFrame);
        }

        @Override
        protected void onException(IOException e) {
            logger.exception(Level.SEVERE, e);
        }

        public void sendInternalError(String msg) throws IOException {
            logger.severe(msg);
            JSONObject errSend = new JSONObject();
            errSend.put("type", "error");
            errSend.put("data",msg);
            send(errSend.toString());
        }

        public void send(String type, String data) throws IOException {
            JSONObject sendJson = new JSONObject();
            sendJson.put("type",type);
            sendJson.put("data",data);
            send(sendJson.toString());
        }
    }
}