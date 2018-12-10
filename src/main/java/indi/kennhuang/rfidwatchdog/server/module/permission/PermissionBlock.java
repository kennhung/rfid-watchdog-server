package indi.kennhuang.rfidwatchdog.server.module.permission;

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
}
