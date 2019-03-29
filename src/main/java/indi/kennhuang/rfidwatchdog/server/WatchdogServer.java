package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.DeviceServer;
import indi.kennhuang.rfidwatchdog.server.system.SystemInfo;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.WebApp;

import java.io.IOException;

public class WatchdogServer {
    public static DeviceServer deviceserver;
    private static WatchDogLogger logger;
    public static WebApp webapp;

    public static void main(String Args[]){
        int webPort = 6084;
        int hwPort = 6083;

        boolean debugEnable = false, debugWeb = false;
        for(String arg :Args){
            if("-debug".equals(arg)){
                debugEnable = true;
            }
            else if(arg.contains("-webPort")){
                webPort = Integer.parseInt(arg.split("=")[1]);
            }
            else if(arg.contains("-hwPort")){
                hwPort = Integer.parseInt(arg.split("=")[1]);
            } else if(arg.contains("-debug-web")){
                debugWeb = true;
            }
        }

        WatchDogLogger.init(debugEnable);
        logger = new WatchDogLogger(LogType.Main);

        deviceserver = new DeviceServer(hwPort);
        new Thread(deviceserver).start();
        SQLite.openDatabase("jdbc:sqlite:foo2.db");

        try {
            webapp = new WebApp(webPort,debugWeb);
        } catch (IOException ioe) {
            new WatchDogLogger(LogType.WebPage).severe("Couldn't start server:\n" + ioe);
        }
        logger.info("Server initialized");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                webapp.stop();
                WatchDogLogger.close();
                System.out.println("Logger close");
            }
        }));
        // close logger file handler before close

        SystemInfo.serverStart();

        while (true){
            //handle console
        }
    }
}
