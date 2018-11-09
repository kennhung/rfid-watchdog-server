package indi.kennhuang.rfidwatchdog.server.util.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.*;


public class LoggerSet {
    public Logger logger;
    public FileHandler fh;
    public ConsoleHandler consoleHandler;

    public LoggerSet(String name, String logFilePath) throws IOException {
        fh = new FileHandler(logFilePath);
        logger = Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        final PrintStream err = System.err;
        System.setErr(System.out);
        try {
            consoleHandler = new ConsoleHandler(); //Snapshot of System.err
        } finally {
            System.setErr(err);
        }
        consoleHandler.setLevel(Level.FINER);
        logger.addHandler(consoleHandler);
        fh.setLevel(Level.FINE);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
