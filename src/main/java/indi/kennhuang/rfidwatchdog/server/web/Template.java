package indi.kennhuang.rfidwatchdog.server.web;

import fi.iki.elonen.NanoHTTPD;
import indi.kennhuang.rfidwatchdog.server.WatchdogServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static fi.iki.elonen.NanoHTTPD.*;

public class Template {
    public static Response getNotFoundResponse() {
        NanoHTTPD.Response r = getTemplateFromFile(Response.Status.NOT_FOUND,"/web/errHtml/404.html");
        if (r == null) {
            r = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Error 404, file not found.");
        }
        return r;
    }

    public static Response getUnauthorizedResponse() {
        Response r = getTemplateFromFile(Response.Status.UNAUTHORIZED,"/web/errHtml/401.html");
        if (r == null) {
            r = newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Error 401, Unauthorized.");
        }
        return r;
    }

    public static Response getInternalErrorResponse(String errorMsg){
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,MIME_PLAINTEXT,"Error 500, Internal Server Error: "+errorMsg);
    }

    public static Response getMethodNotAllowedResponse(){
        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED,MIME_PLAINTEXT,"Error 405, Method Not Allowed.");
    }

    public static Response getForbiddenResponse(){
        Response r = getTemplateFromFile(Response.Status.FORBIDDEN,"/web/errHtml/403.html");
        if (r == null) {
            r = newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "Error 403, Forbidden.");
        }
        return r;
    }

    private static Response getTemplateFromFile(Response.Status status, String path){
        Response r = null;
        InputStream in = WatchdogServer.class.getResourceAsStream(path);
        if(in != null){
            try {
                r = newFixedLengthResponse(status, MIME_HTML, new String(IOUtils.toByteArray(in)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return r;
    }
}
