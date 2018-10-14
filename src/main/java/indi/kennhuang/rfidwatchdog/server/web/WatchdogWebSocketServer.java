package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoWSD;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;

import java.io.IOException;
import java.util.logging.Level;

public class WatchdogWebSocketServer extends NanoWSD {

    WatchDogLogger logger = null;

    public WatchdogWebSocketServer(int port, WatchDogLogger logger) {
        super(port);
        this.logger = logger;
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new WatchdogWebSocket(handshake, logger);
    }

    private static class WatchdogWebSocket extends WebSocket {

        WatchDogLogger logger;

        public WatchdogWebSocket(IHTTPSession handshakeRequest, WatchDogLogger logger) {
            super(handshakeRequest);
            this.logger = logger;
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

        }

        @Override
        protected void onPong(WebSocketFrame webSocketFrame) {
            logger.debug("P " + webSocketFrame);
        }

        @Override
        protected void onException(IOException e) {
            logger.exception(Level.SEVERE, e);
        }
    }
}
