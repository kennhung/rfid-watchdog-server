package indi.kennhuang.rfidwatchdog.server.system;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.util.*;

public class NetworkInterfaceInfo {

    public static JSONArray getNetworkInterfaceInfo(boolean v4, boolean v6, boolean local) throws SocketException {
        JSONArray networkInterfacesInfo = new JSONArray();
        Enumeration eni = NetworkInterface.getNetworkInterfaces();
        while (eni.hasMoreElements()) {
            NetworkInterface networkCard = (NetworkInterface) eni.nextElement();
            if (!networkCard.isVirtual() && networkCard.isUp()) {
                if(!local && networkCard.isLoopback()) continue;

                StringBuilder macAddress = new StringBuilder();
                if(!networkCard.isLoopback()){
                    byte[] mac = networkCard.getHardwareAddress();
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                }
                else macAddress.append("N/A");

                JSONObject card = new JSONObject();
                card.put("address",getInterfaceAddress(networkCard,v4,v6));
                card.put("name",networkCard.getName());
                card.put("displayName", networkCard.getDisplayName());
                card.put("mac", macAddress.toString());

                networkInterfacesInfo.put(card);
            }
        }
        return networkInterfacesInfo;
    }

    public static JSONArray getNetworkInterfaceInfoWithAddress(InetAddress Inetaddress, boolean v4, boolean v6) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(Inetaddress);
        return getInterfaceAddress(networkInterface,v4,v6);
    }

    public static JSONArray getInterfaceAddress(NetworkInterface networkInterface, boolean v4, boolean v6) throws SocketException {
        JSONArray outJson = new JSONArray();
        Iterator addressIterator = networkInterface.getInterfaceAddresses().iterator();
        while (addressIterator.hasNext()) {
            InterfaceAddress networkCardAddress = (InterfaceAddress) addressIterator.next();
            InetAddress address = networkCardAddress.getAddress();
            JSONObject addressInfo = new JSONObject();

            String hostAddress = address.getHostAddress();
            String maskAddress = "N/A";
            String subnetAddress = "N/A";
            String ipProtocol = "N/A";

            if (!address.isLoopbackAddress()) {
                if (hostAddress.indexOf(":") > 0) {
                    // Ipv6
                    if(!v6) continue;
                    ipProtocol = "IPv6";
                } else {
                    // Ipv4
                    if(!v4) continue;
                    ipProtocol = "IPv4";
                    maskAddress = calcMaskByPrefixLength(networkCardAddress.getNetworkPrefixLength());
                    subnetAddress = calcSubnetAddress(hostAddress, maskAddress);
                }
            } else {
                ipProtocol = "loopback";
            }


            addressInfo.put("address", hostAddress);
            addressInfo.put("maskAddress", maskAddress);
            addressInfo.put("subnetAddress", subnetAddress);
            addressInfo.put("ipProtocol", ipProtocol);
            outJson.put(addressInfo);
        }
        return outJson;
    }

    public static String calcMaskByPrefixLength(int length) {
//        System.out.println("Test>" + length);
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    public static String calcSubnetAddress(String ip, String mask) {
        String result = "";
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }

            // make result string
            result = result + resultRaw[0];
            for (int i = 1; i < resultRaw.length; i++) {
                result = result + "." + resultRaw[i];
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return result;
    }
}
