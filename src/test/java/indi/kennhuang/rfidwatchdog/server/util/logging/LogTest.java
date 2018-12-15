package indi.kennhuang.rfidwatchdog.server.util.logging;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Level;


public class LogTest {

    @BeforeClass
    public static void setup(){
        WatchDogLogger.init(true);
        SQLite.openDatabase("jdbc:sqlite:test.db");
    }

    @AfterClass
    public static void tearDown() {
        SQLite.closeDatabase();
    }

    @Test
    public void TestLoggerInit(){
        WatchDogLogger log = new WatchDogLogger(LogType.WatchDogLogger);
        log.log(Level.FINEST,"Test");
    }
}
