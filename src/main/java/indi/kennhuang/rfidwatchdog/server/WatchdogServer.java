package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.DeviceServer;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import indi.kennhuang.rfidwatchdog.server.web.WebApp;

import java.io.IOException;

public class WatchdogServer {
    private static DeviceServer deviceserver = new DeviceServer();
    private static WatchDogLogger logger;

    public static void main(String Args[]){
        WatchDogLogger.init();
        logger = new WatchDogLogger(LogType.Main);
        new Thread(deviceserver).start();
        SQLite.openDatabase("jdbc:sqlite:foo.db");

        boolean webDebug = false;
        for(String arg :Args){
            if(arg.equals("-debug")){
                webDebug = true;
            }
        }

        try {
            new WebApp(6084,webDebug);
        } catch (IOException ioe) {
            new WatchDogLogger(LogType.WebPage).severe("Couldn't start server:\n" + ioe);
        }
        logger.info("Server initialized");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                WatchDogLogger.close();
                System.out.println("Logger close");
            }
        }));
        // close logger file handler before close

        while (true){

        }
    }

}
