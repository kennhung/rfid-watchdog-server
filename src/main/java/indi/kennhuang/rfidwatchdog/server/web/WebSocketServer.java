package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoWSD;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.wsHandler.indexHandler;
import org.json.JSONException;
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
            //TODO route

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
            }
            catch(JSONException jsonE){
                try {
                    sendInternalError(jsonE.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            handler.route(recJson.getInt("type"),recJson.getString("message"));
        }

        @Override
        protected void onPong(WebSocketFrame webSocketFrame) {
            logger.finest("P " + webSocketFrame);
        }

        @Override
        protected void onException(IOException e) {
            logger.exception(Level.SEVERE, e);
        }

        private void sendInternalError(String msg) throws IOException {
            JSONObject errSend = new JSONObject();
            errSend.put("type",0);
            errSend.put("errorMsg",msg);
            send(errSend.toString());
        }
    }
}
