package indi.kennhuang.rfidwatchdog.server.util.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.security.AlgorithmConstraints;
import java.util.logging.*;


public class LoggerSet {
    public Logger logger;
    public FileHandler fh;

    public LoggerSet(String name, String logFilePath) throws IOException {
        fh = new FileHandler(logFilePath);
        logger = Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = null;
        final PrintStream err = System.err;
        System.setErr(System.out);
        try {
            consoleHandler = new ConsoleHandler(); //Snapshot of System.err
        } finally {
            System.setErr(err);
        }
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
