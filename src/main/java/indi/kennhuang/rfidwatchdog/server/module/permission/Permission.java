package indi.kennhuang.rfidwatchdog.server.module.permission;

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
}
