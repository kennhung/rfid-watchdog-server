package indi.kennhuang.rfidwatchdog.server.system;

import org.json.JSONObject;

import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;

public class SystemInfo {
    public static final String systemVersion = "1.0 beta-1";
    private static boolean started = false;

    private static Instant startupTime;

    public static void serverStart() {
        if (!started) {
            startupTime = Instant.now();
//            TemperatureInfo.start();
        }
    }

    public static JSONObject getInfoInJson() throws SocketException {
        JSONObject infoOut = new JSONObject();

        infoOut.put("uptime", getSystemUptimeInSec());
        infoOut.put("sysVersion", systemVersion);
        infoOut.put("network", NetworkInterfaceInfo.getNetworkInterfaceInfo(true,false,false).toString());
        infoOut.put("temp", 0);
        return infoOut;
    }


    public static Duration getSystemUptime() {
        return Duration.between(startupTime, Instant.now());
    }

    public static long getSystemUptimeInSec() {
        return getSystemUptime().abs().getSeconds();
    }
}
