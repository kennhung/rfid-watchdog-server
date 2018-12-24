package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.devices.util.DoorUtil;
import indi.kennhuang.rfidwatchdog.server.protocal.hardware.HardwareMessage;
import indi.kennhuang.rfidwatchdog.server.protocal.hardware.enums.TypesEnum;
import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceHandler implements Runnable {
    public final static int pingPeriod = 30;

    private Socket clientSocket;
    private Timer pingTimer;
    private DataOutputStream output = null;

//    private String auth_token;
    private boolean auth;
//    private int connDoor_id;
    private boolean looping = true;

    private WatchDogLogger logger;

    public DeviceHandler(Socket s) {
        clientSocket = s;
        auth = false;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(clientSocket.getRemoteSocketAddress().toString());
        logger = new WatchDogLogger(LogType.HardwareServer);
        logger.info("Incoming connection from " + clientSocket.getRemoteSocketAddress());
        StringBuilder inputBuffer = new StringBuilder();
        DataInputStream input = null;
        try {
            input = new DataInputStream(this.clientSocket.getInputStream());
            output = new DataOutputStream(this.clientSocket.getOutputStream());
            auth = true;//TODO remove after auth finish;

            pingTimer = new Timer();
            pingTimer.schedule(new PingTask(), pingPeriod * 1000);

            Instant lastPing = Instant.now();
            while (looping) {
                boolean doReply = true;
                long pingTime = Duration.between(lastPing, Instant.now()).abs().getSeconds();
                if (pingTime > 35) {
                    pingTimer.cancel();
                    break;
                }

                if (input.available() > 0) {
                    int buf = input.read();
                    if (buf == -1) {
                        break;
                    } else if (buf == ';') {
                        logger.debug(inputBuffer.toString());
                        HardwareMessage message = HardwareMessage.encodeMessage(inputBuffer.toString());
                        HardwareMessage reply = new HardwareMessage();
                        if (auth || message.type == HardwareMessage.types.AUTH) {
                            switch (message.type) {
                                case CARD_CHECK:
                                    reply.type = TypesEnum.types.RESPONSE;
                                    JSONObject checkResult = DoorUtil.check(message.content, logger);
                                    reply.content = new JSONObject().put("reply", checkResult.toString());
                                    System.out.println(HardwareMessage.decodeMessage(reply));
                                    break;
                                case PONG:
                                    lastPing = Instant.now();
                                    doReply = false;
                                    break;
                                case AUTH:

                                    break;
                                default:
                                    System.out.println("Wrong type: " + message.type);
                                    break;
                            }
                        }

                        if (doReply) {
                            output.write((HardwareMessage.decodeMessage(reply) + ";").getBytes());
                            output.flush();
                        }
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
                pingTimer.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(clientSocket.getRemoteSocketAddress() + " disconnected");
        }
    }

    class PingTask extends TimerTask {
        public void run() {
            HardwareMessage pingMessage = new HardwareMessage();
            pingMessage.type = TypesEnum.types.PING;
            try {
                output.write((HardwareMessage.decodeMessage(pingMessage) + ";").getBytes());
                output.flush();
            } catch (IOException e) {
                if (e.getMessage().equals("Connection reset by peer: socket write error") && !e.getMessage().equals("Socket closed")) {
                    logger.fine("Ping send fail");
                } else {
                    e.printStackTrace();
                }
                looping = false;
                return;
            }
            pingTimer.schedule(new PingTask(), pingPeriod * 1000);
        }
    }
}
