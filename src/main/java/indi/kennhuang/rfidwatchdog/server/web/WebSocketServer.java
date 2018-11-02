package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoWSD;
import indi.kennhuang.rfidwatchdog.server.protocal.websocket.WebSocketHandler;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.wsHandler.EmptyHandler;
import indi.kennhuang.rfidwatchdog.server.web.wsHandler.UsersHandler;
import indi.kennhuang.rfidwatchdog.server.web.wsHandler.indexHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class WebSocketServer extends NanoWSD {

    private WatchDogLogger logger;

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
            logger.debug("WS Connect '" + uri + "'");
            //TODO serve

            if (uri.equals("/") || uri.equals("/index")) {
                handler = new indexHandler(this);
            }else if(uri.equals("/users")){
                handler = new UsersHandler(this);
            }
            else {
                handler = new EmptyHandler();
            }
            logger.debug("serving with handler: " + handler.getName());

            new Thread(() -> {
                while (true) {
                    try {
                        ping("Ping".getBytes());
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (e.getMessage().equals("Socket closed") || e.getMessage().contains("socket write error")) {
                            break;
                        } else {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e){
                        break;
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
            try {
                logger.debug("M " + webSocketFrame);
                String receive = webSocketFrame.getTextPayload();
                JSONObject recJson;
                String msgType = null;
                String data;
                try {
                    recJson = new JSONObject(receive);

                    msgType = recJson.getString("type");
                    data = recJson.getString("data");
                    handler.getClass().getMethod(msgType, String.class).invoke(handler, data);
                } catch (JSONException | IllegalAccessException | InvocationTargetException e) {
                    sendInternalError(e.getMessage());
                } catch (NoSuchMethodException e) {
                    sendInternalError("Can't fine method of " + msgType);
                }
            } catch (IOException IOE) {
                IOE.printStackTrace();
            }
        }

        @Override
        protected void onPong(WebSocketFrame webSocketFrame) {
            logger.debug("P " + webSocketFrame);
        }

        @Override
        protected void onException(IOException e) {
            if (e.getMessage().equals("Socket closed") || e.getMessage().contains("socket write error")) {
                logger.debug("Socket closed");
            } else {
                logger.exception(Level.SEVERE, e);
            }
        }

        public void sendInternalError(String msg) throws IOException {
            logger.severe(msg);
            JSONObject errSend = new JSONObject();
            errSend.put("type", "error");
            errSend.put("data", msg);
            send(errSend.toString());
        }

        public void send(String type, String data) throws IOException {
            JSONObject sendJson = new JSONObject();
            sendJson.put("type", type);
            sendJson.put("data", data);
            send(sendJson.toString());
        }
    }
}
