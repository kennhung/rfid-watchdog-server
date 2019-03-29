package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import indi.kennhuang.rfidwatchdog.server.WatchdogServer;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebApp extends NanoHTTPD {
    private boolean quiet;
    private boolean detail;
    private WatchDogLogger logger;
    private Map<String, Integer> authFailCount = new HashMap<String, Integer>();
    private NanoWSD ws;

    private static final int authFailTime = 2;

    public WebApp(int port, boolean debug) throws IOException {
        super(port);
        logger = new WatchDogLogger(LogType.WebPage);
        quiet = false;
        detail = false;
        if(!debug){
            quiet = true;
        }
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Running! Point your browsers to http://localhost:" + port + "/");
        ws = new WebSocketServer(6085,logger, quiet);
        ws.start();
    }

    @Override
    public void stop(){
        super.stop();
        ws.stop();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        Map<String, List<String>> parms = session.getParameters();
        String uri = session.getUri();
        String auth = header.get("authorization");

        Iterator e;
        if (!this.quiet) {
            logger.debug(session.getMethod() + " '" + uri + "'  " + "authorization: " + auth);
            if (this.detail) {
                e = header.keySet().iterator();

                String value;
                while (e.hasNext()) {
                    value = (String) e.next();
                    System.out.println("  HDR: '" + value + "' = '" + (String) header.get(value) + "'");
                }

                e = parms.keySet().iterator();

                while (e.hasNext()) {
                    value = (String) e.next();
                    System.out.println("  PRM: '" + value + "' = '" + (String) parms.get(value).get(0) + "'");
                }
            }
        }

        if (uri.equals("/")) {
            // index
            uri = "/index.html";
        }
        else if(uri.contains("/errHtml/")){
            // 403
            return Template.getForbiddenResponse();
        }
        // serve special uri

        if(!session.getMethod().equals(Method.GET)){
            return Template.getMethodNotAllowedResponse();
        }
        // 405 Method Not Allowed

        InputStream in = WatchdogServer.class.getResourceAsStream("/web" + uri);
        Response r = null;

        String authToken;
        if (auth == null) {
            authToken = "";
        } else {
            authToken = auth.split(" ")[1];
        }
        // handle authToken

        byte[] fileReadIn;
        if (in == null) {
            // File not found
            r = Template.getNotFoundResponse();
        } else if (uri.contains("/assets/")) {
            // Serve resource files
            try {
                fileReadIn = IOUtils.toByteArray(in);
                r = newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(uri), new String(fileReadIn, StandardCharsets.UTF_8));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (authToken.equals(new String(Base64.getEncoder().encode("root:root".getBytes())))) {
            // Auth passed
            try {
                fileReadIn = IOUtils.toByteArray(in);
                r = newFixedLengthResponse(Response.Status.OK, MIME_HTML, new String(fileReadIn, StandardCharsets.UTF_8));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            // No auth return 401 error
            String remoteAddress = header.get("remote-addr");
            r = Template.getUnauthorizedResponse();

            if (!authFailCount.containsKey(remoteAddress)) {
                authFailCount.put(remoteAddress, 0);
            }

            if (authFailCount.get(remoteAddress) < authFailTime) {
                authFailCount.put(remoteAddress, authFailCount.get(remoteAddress) + 1);
                r.addHeader("WWW-Authenticate", "Basic");
            } else {
                authFailCount.put(remoteAddress, -authFailTime);
            }
        }

        return r;
    }
}
