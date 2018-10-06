package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoHTTPD;
import indi.kennhuang.rfidwatchdog.server.WatchdogServer;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebApp extends NanoHTTPD {
    private boolean quiet;
    private boolean detail;
    private WatchDogLogger logger;

    public WebApp(int port) throws IOException {
        super(port);
        logger = new WatchDogLogger(LogType.WebPage);
        quiet = false;
        detail = false;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Running! Point your browsers to http://localhost:" + port + "/");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        Map<String, List<String>> parms = session.getParameters();
        String uri = session.getUri();

        Iterator e;
        if (!this.quiet) {
            logger.debug(session.getMethod() + " '" + uri + "' ");
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
            uri = "/index.html";
        }
        //route special uri

        InputStream in = WatchdogServer.class.getResourceAsStream("/web" + uri);
        Response r = null;

        byte[] fileReadIn;
        if (in == null) {
            r = getNotFoundResponse();
        } else {
            try {
                fileReadIn = IOUtils.toByteArray(in);
                if(uri.contains("/css/")||uri.contains("/js/")){
                    r = newFixedLengthResponse(Response.Status.OK,MIME_PLAINTEXT,new String(fileReadIn));
                }
                else {
                    r = newFixedLengthResponse(Response.Status.OK,MIME_HTML,new String(fileReadIn));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return r;
    }

    private Response getNotFoundResponse(){
        Response r = null;
        InputStream in = WatchdogServer.class.getResourceAsStream("/web/404.html");
        if (in == null) {
            r = newFixedLengthResponse(Response.Status.NOT_FOUND,MIME_PLAINTEXT,"Error 404, file not found.");
        } else {
            try {
                r = newFixedLengthResponse(Response.Status.NOT_FOUND,MIME_HTML,new String(IOUtils.toByteArray(in)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return r;
    }

}
