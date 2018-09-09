package indi.kennhuang.rfidwatchdog.server;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.devices.DeviceServer;

public class WatchdogServer {
    static DeviceServer deviceserver = new DeviceServer();

    public static void main(String Args[]){
        new Thread(deviceserver).start();
        SQLite.openDatabase("jdbc:sqlite:foo.db");
    }
}
