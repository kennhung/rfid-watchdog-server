package indi.kennhuang.rfidwatchdog.server.module.permission;

import org.json.JSONObject;

public class Permission {
    public boolean open;
    public boolean admin;

    public Permission(){
        this.open = false;
        this.admin = false;
    }

    public Permission(boolean open, boolean admin){
        this.open = open;
        this.admin = admin;
    }

    public static Permission encodePermission(JSONObject json){
        Permission p = new Permission();
        p.admin = json.getBoolean("admin");
        p.open = json.getBoolean("open");
        return p;
    }

    public static JSONObject decodePermission(Permission p){
        JSONObject out = new JSONObject();
        out.put("open",p.open);
        out.put("admin",p.admin);
        return out;
    }

}
