package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.db.Group;
import indi.kennhuang.rfidwatchdog.server.db.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class DeviceHandler implements Runnable {
    private Socket clientSocket;

    public DeviceHandler(Socket s) {
        clientSocket = s;
    }

    @Override
    public void run() {
        System.out.printf("Incoming connection from %s\n", clientSocket.getRemoteSocketAddress());
        DataInputStream input = null;
        DataOutputStream output = null;
        StringBuffer inputBuffer = new StringBuffer();
        try {
            input = new DataInputStream(this.clientSocket.getInputStream());
            output = new DataOutputStream(this.clientSocket.getOutputStream());

            while (true) {
                int buf = input.read();
                if (buf == -1) {
                    break;
                } else if (buf == ';') {
                    System.out.println(inputBuffer.toString());
                    JSONObject json = new JSONObject(inputBuffer.toString());
                    JSONObject reply = new JSONObject();

                    // Set reply data
                    reply.put("type", "doorAccess");
                    reply.put("ReplyServer", clientSocket.getInetAddress());

                    try {
                        User resUser = User.findUserByUid(json.getString("uid"));
                        if (resUser == null) {
                            reply.put("open", false);
                            reply.put("name", "Unknown");
                        } else {
                            if (checkDoorsAccessibility(new JSONArray(resUser.doors), json.getInt("doorId"), 1)) {
                                reply.put("open", true);
                            } else {
                                if (checkGroupDoorsAccessibility(new JSONArray(resUser.groups), json.getInt("doorId"), 1)) {
                                    reply.put("open", true);
                                } else {
                                    reply.put("open", false);
                                }
                            }
                            reply.put("name", resUser.name);
                        }
                        output.write((reply.toString()+";").getBytes());
                        System.out.println(reply.toString());
                        output.flush();
                        inputBuffer.delete(0, inputBuffer.length());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    inputBuffer.append((char) buf);
                }

                Thread.sleep(10);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    private boolean checkDoorsAccessibility(JSONArray doors, int doorId, int level) {
        for (int i = 0; i < doors.length(); i++) {
            JSONObject door = (JSONObject) doors.get(i);
            if (doorId == door.getInt("doorId")) {
                if (door.getInt("level") > level) {
                    //Access Granted
                    return true;
                }
            }
        }
        //Access Denied
        return false;
    }

    private boolean checkGroupDoorsAccessibility(JSONArray groups, int doorId, int level) {
        for (int i = 0; i < groups.length(); i++) {
            try {
                Group group = Group.findGroupById(groups.getInt(i));
                if(group == null){
                    continue;
                }
                if (checkDoorsAccessibility(new JSONArray(group.doors), doorId, level)) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
