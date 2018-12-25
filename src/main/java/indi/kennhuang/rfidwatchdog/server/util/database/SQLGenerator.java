package indi.kennhuang.rfidwatchdog.server.util.database;

public class SQLGenerator {

    public static String getSelectString(String select, String from, String field, String target) {
        String out = "SELECT " + select + " FROM " + from;

        if (field != null) {
            out += " where " + field + " is " + target;
        }

        return out;
    }

}
