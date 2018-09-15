package indi.kennhuang.rfidwatchdog.server.protocal;

import indi.kennhuang.rfidwatchdog.server.protocal.enums.TypesEnum;
import org.json.JSONObject;

public class HardwareMessage extends TypesEnum {
    public JSONObject content;
    public int type;

    public HardwareMessage(String msg){
        content = new JSONObject(msg);
        type = content.getInt("type");
    }
}
