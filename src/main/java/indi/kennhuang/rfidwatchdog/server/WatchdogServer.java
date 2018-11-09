package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.DeviceServer;
import indi.kennhuang.rfidwatchdog.server.system.SystemInfo;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.WebApp;

import java.io.IOException;

public class WatchdogServer {
    private static DeviceServer deviceserver = new DeviceServer();
    private static WatchDogLogger logger;
    private static WebApp webapp;

    public static void main(String Args[]){

        boolean debugEnable = false;
        for(String arg :Args){
            if("-debug".equals(arg)){
                debugEnable = true;
            }
        }

        WatchDogLogger.init(debugEnable);
        logger = new WatchDogLogger(LogType.Main);
        new Thread(deviceserver).start();
        SQLite.openDatabase("jdbc:sqlite:foo.db");

        try {
            webapp = new WebApp(6084,debugEnable);
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
