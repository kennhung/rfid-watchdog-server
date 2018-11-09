package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.protocal.hardware.HardwareMessage;
import indi.kennhuang.rfidwatchdog.server.protocal.hardware.enums.TypesEnum;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static indi.kennhuang.rfidwatchdog.server.devices.DeviceServer.serverPort;
import static org.junit.Assert.assertFalse;

public class TestHardwareServer {
    Timer timer = null;
    Socket socket = null;
    boolean closed = false;


    public void testPing() throws InterruptedException {
        WatchDogLogger.init(true);
        new Thread(new DeviceServer()).start();
        SQLite.openDatabase("jdbc:sqlite:test.db");

        try {
            socket = new Socket("127.0.0.1", serverPort);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            timer = new Timer();
            timer.schedule(new task(), 60 * 1000);

            StringBuilder inputBuffer = new StringBuilder();
            while (true) {
                int buf = in.read();
                if (buf == -1) {
                    closed = true;
                    break;
                } else if (buf == ';') {
                    System.out.println("[Test: receive] " + inputBuffer.toString());
                    HardwareMessage message = HardwareMessage.encodeMessage(inputBuffer.toString());
                    HardwareMessage reply = new HardwareMessage();
                    if (message.type == TypesEnum.types.PING) {
                        reply.type = TypesEnum.types.PONG;
                        output.write((HardwareMessage.decodeMessage(reply) + ";").getBytes());
                        output.flush();
                        System.out.println("[Test:  reply]" + (HardwareMessage.decodeMessage(reply) + ";"));
                    }
                    inputBuffer.delete(0, inputBuffer.length());
                } else {
                    inputBuffer.append((char) buf);
                }

                Thread.sleep(10);
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Socket closed")) {
                System.out.println("disconnected");
            } else {
                e.printStackTrace();
            }
        }

        SQLite.closeDatabase();
    }

    class task extends TimerTask {

        @Override
        public void run() {
            assertFalse(closed);
            timer.cancel();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
