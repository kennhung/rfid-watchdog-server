package indi.kennhuang.rfidwatchdog.server.util.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatchDogLogger {
    private static Map<LogType, LoggerSet> loggers = new HashMap<LogType, LoggerSet>();
    private static boolean init = false;

    private Logger logger;
    private LogType logType;

    public static void init() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n");
        //set log time format

        if (!init) {
            for (LogType type : LogType.values()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH-mm-ss");
                String filePath = "log/"+type.name()+"/"+sdf.format(Calendar.getInstance().getTime())+".log";

                try {
                    Path path = Paths.get(filePath);
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);

                    loggers.put(type, new LoggerSet(type.name(),filePath));
                    loggers.get(type).logger.info("Logger Start");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            init = true;
        }
    }

    public WatchDogLogger(LogType type) {
        logger = loggers.get(type).logger;
        logger.setLevel(Level.ALL);
    }
    public WatchDogLogger(LogType type, Level consoleLevel, Level fhLevel){
        logger = loggers.get(type).logger;
        logger.setLevel(consoleLevel);
        loggers.get(type).fh.setLevel(fhLevel);
    }

    public void log(Level level, String msg){
        logger.log(level, "["+Thread.currentThread().getName()+"] "+msg);
    }

    public void severe(String msg){
        log(Level.SEVERE,msg);
    }

    public void warning(String msg){
        log(Level.WARNING,msg);
    }

    public void info(String msg){
        log(Level.INFO,msg);
    }

    public void config(String msg){
        log(Level.CONFIG,msg);
    }

    public void fine(String msg){
        log(Level.FINE,msg);
    }

    public void finer(String msg){
        log(Level.FINER,msg);
    }

    public void finest(String msg){
        log(Level.FINEST,msg);
    }

    public void debug(String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        System.out.println("["+sdf.format(Calendar.getInstance().getTime())+"] [DEBUG] ["+Thread.currentThread().getName()+"] "+msg);
    }

}
