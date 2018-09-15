package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.protocal.HardwareMessage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class DeviceHandler implements Runnable {
    private Socket clientSocket;
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
        DataInputStream input = null;
        DataOutputStream output = null;
        StringBuilder inputBuffer = new StringBuilder();
        try {
            input = new DataInputStream(this.clientSocket.getInputStream());
            output = new DataOutputStream(this.clientSocket.getOutputStream());
            auth = true;
            //TODO remove after auth finish;
            while (true) {
                //TODO add ping function
                if (input.available() > 0) {
                    int buf = input.read();
                    if (buf == -1) {
                        break;
                    } else if (buf == ';') {
                        System.out.println(inputBuffer.toString());

                        HardwareMessage message = new HardwareMessage(inputBuffer.toString());
                        JSONObject reply = new JSONObject();
                        if (auth || message.type == HardwareMessage.AUTH) {
                            switch (message.type) {
                                case HardwareMessage.CARD_CHECK:
                                    reply = CheckDoorPermission.check(message.content);
                                    System.out.println(reply.toString());
                                    break;
                                case HardwareMessage.RESPONSE:

                                    break;
                                case HardwareMessage.AUTH:

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

}
