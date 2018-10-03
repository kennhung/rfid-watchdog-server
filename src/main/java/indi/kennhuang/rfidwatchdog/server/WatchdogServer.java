package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.DeviceServer;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;

public class WatchdogServer {
    private static DeviceServer deviceserver = new DeviceServer();
    private static WatchDogLogger logger = new WatchDogLogger(LogType.Main);

    public static void main(String Args[]){
        WatchDogLogger.init();
        new Thread(deviceserver).start();
        SQLite.openDatabase("jdbc:sqlite:foo.db");
    }
}
