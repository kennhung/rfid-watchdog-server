package indi.kennhuang.rfidwatchdog.server.util;

import java.time.Duration;
import java.time.Instant;

public class SystemInfo {
    public static final String systemVersion = "1.0 SNAPSHOT";
    private static boolean started = false;

    private static Instant startupTime;


    public static void serverStart(){
        if(!started){
            startupTime = Instant.now();
        }
    }

    public static Duration getSystemUptime(){
        return Duration.between(startupTime, Instant.now());
    }
}
