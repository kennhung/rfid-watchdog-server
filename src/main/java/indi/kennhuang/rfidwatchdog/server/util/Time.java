package indi.kennhuang.rfidwatchdog.server.util;

public class Time {
    public static long getUNIXTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

}
