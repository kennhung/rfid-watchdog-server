package indi.kennhuang.rfidwatchdog.server.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerSet {
    public Logger logger;
    public FileHandler fh;

    public LoggerSet(String name, String logFilePath) throws IOException {
        fh = new FileHandler(logFilePath);
        logger = Logger.getLogger(name);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
