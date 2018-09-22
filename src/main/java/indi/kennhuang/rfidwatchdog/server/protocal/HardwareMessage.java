package indi.kennhuang.rfidwatchdog.server.protocal;

import indi.kennhuang.rfidwatchdog.server.protocal.enums.TypesEnum;
import org.json.JSONObject;

public class HardwareMessage extends TypesEnum {
    public JSONObject content;
    public types type;

    public HardwareMessage(){

    }

    public HardwareMessage(types type){
        this.type = type;
    }

    public static HardwareMessage encodeMessage(String msg){
        HardwareMessage message = new HardwareMessage();
        message.content = new JSONObject(msg);
        message.type = encode(message.content.getInt("type"));
        return message;
    }
}
