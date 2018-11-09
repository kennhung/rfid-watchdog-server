package indi.kennhuang.rfidwatchdog.server.util.logging;

import org.junit.Test;

import java.util.logging.Level;


public class LogTest {

    @Test
    public void TestLoggerInit(){
        WatchDogLogger.init(true);
        WatchDogLogger log = new WatchDogLogger(LogType.WatchDogLogger);
        log.log(Level.FINEST,"Test");
    }
}
