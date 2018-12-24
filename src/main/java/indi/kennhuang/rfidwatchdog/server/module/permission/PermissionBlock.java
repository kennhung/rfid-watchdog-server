package indi.kennhuang.rfidwatchdog.server.module.permission;

import org.json.JSONObject;

public class PermissionBlock {
    public int targetId;
    public Permission permission;
    public long validate;

    public PermissionBlock(){
        targetId = 0;
        permission = new Permission();
        validate = 0;
    }

    public PermissionBlock(int target, long validate, Permission permission){
        this.permission = permission;
        this.validate = validate;
        targetId = target;
    }

    public static PermissionBlock encodePermissionBlock(JSONObject json){
        PermissionBlock pb = new PermissionBlock();
        pb.permission = Permission.encodePermission(json.getJSONObject("permission"));
        pb.validate = json.getLong("validate");
        pb.targetId = json.getInt("targetId");
        return pb;
    }

    public static JSONObject decodePermissionBlock(PermissionBlock pb){
        JSONObject out = new JSONObject();
        out.put("targetId",pb.targetId);
        out.put("validate",pb.validate);
        out.put("permission", Permission.decodePermission(pb.permission));
        return out;
    }
}
