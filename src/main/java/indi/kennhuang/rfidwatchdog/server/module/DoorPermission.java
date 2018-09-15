package indi.kennhuang.rfidwatchdog.server.module;

import java.util.Calendar;

public class DoorPermission {
    public int doorId;
    public long validDate;
    public boolean open;

    public DoorPermission(int id, boolean open, long validDate) {
        doorId = id;
        this.open = open;
        this.validDate = validDate;
    }

    public DoorPermission(int id, boolean open) {
        doorId = id;
        this.open = open;
        this.validDate = System.currentTimeMillis() + (31536000 * 10) / 1000L;
    }
}
