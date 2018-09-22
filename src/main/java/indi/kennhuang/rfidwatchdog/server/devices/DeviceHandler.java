package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.devices.util.DoorUtil;
import indi.kennhuang.rfidwatchdog.server.protocal.HardwareMessage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceHandler implements Runnable {
    public final static int pingPeriod = 5;

    private Socket clientSocket;
    private Timer pingTimer;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    private String auth_token;
    private boolean auth;
    private int connDoor_id;


    public DeviceHandler(Socket s) {
        clientSocket = s;
        auth = false;
    }

    @Override
    public void run() {
        System.out.printf("Incoming connection from %s\n", clientSocket.getRemoteSocketAddress());
        StringBuilder inputBuffer = new StringBuilder();
        try {
            input = new DataInputStream(this.clientSocket.getInputStream());
            output = new DataOutputStream(this.clientSocket.getOutputStream());
            auth = true;//TODO remove after auth finish;

            pingTimer = new Timer();
            pingTimer.schedule(new PingTask(), pingPeriod * 1000);

            while (true) {
                if (input.available() > 0) {
                    int buf = input.read();
                    if (buf == -1) {
                        break;
                    } else if (buf == ';') {
                        System.out.println(inputBuffer.toString());
                        HardwareMessage message = HardwareMessage.encodeMessage(inputBuffer.toString());
                        JSONObject reply = new JSONObject();
                        if (auth || message.type == HardwareMessage.types.AUTH) {
                            switch (message.type) {
                                case CARD_CHECK:
                                    reply = DoorUtil.check(message.content);
                                    System.out.println(reply.toString());
                                    break;
                                case RESPONSE:

                                    break;
                                case AUTH:

                                    break;
                                default:
                                    System.out.println("Wrong type: " + message.type);
                                    break;
                            }
                        }

                        output.write((reply.toString() + ";").getBytes());
                        output.flush();
                        inputBuffer.delete(0, inputBuffer.length());
                    } else {
                        inputBuffer.append((char) buf);
                    }
                }

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (this.clientSocket != null && !this.clientSocket.isClosed())
                    this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(clientSocket.getRemoteSocketAddress() + " disconnected");
        }

    }

    class PingTask extends TimerTask {
        public void run() {

        }
    }
}
