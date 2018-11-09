package indi.kennhuang.rfidwatchdog.server.protocal.hardware;

import indi.kennhuang.rfidwatchdog.server.protocal.hardware.enums.TypesEnum;
import org.json.JSONObject;

public class HardwareMessage extends TypesEnum {
    public JSONObject content;
    public types type;

    public HardwareMessage(){
        this.content = new JSONObject();
    }

    public HardwareMessage(types type){
        this.content = new JSONObject();
        this.type = type;
    }

    public static HardwareMessage encodeMessage(String in){
        HardwareMessage out = new HardwareMessage();
        out.content = new JSONObject(in);
        out.type = encode(out.content.getInt("type"));
        out.content.remove("type");
        return out;
    }

    public static String decodeMessage(HardwareMessage in){
        JSONObject out = in.content;
        out.put("type", decode(in.type));
        return out.toString();
    }
}
