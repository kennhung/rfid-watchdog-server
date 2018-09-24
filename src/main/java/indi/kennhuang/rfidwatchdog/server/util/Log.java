package indi.kennhuang.rfidwatchdog.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
    public static void log(String msg){
        sendToConsole("Log",msg);
    }

    private static void sendToConsole(String level, String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        System.out.println("["+level+"]["+sdf.format(Calendar.getInstance().getTime())+"]["+Thread.currentThread().getName()+"] "+msg);
    }
}
