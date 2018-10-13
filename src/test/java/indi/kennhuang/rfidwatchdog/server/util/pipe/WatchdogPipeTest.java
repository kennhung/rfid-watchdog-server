package indi.kennhuang.rfidwatchdog.server.util.pipe;

import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WatchdogPipeTest {

    @Test
    public void testPipe(){
        WatchdogPipe pipe = null;
        try {
            pipe = new WatchdogPipe();
            String send = "Test";
            System.out.println(send);
            pipe.send(send);
            Thread.sleep(1000);
            String rec = pipe.listen();
            System.out.println(rec.toString());
            assertTrue(send.equals(rec));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
