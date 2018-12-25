package indi.kennhuang.rfidwatchdog.server.util.database;

public class SQLGenerator {

    public static String getSelectString(String select, String from, String field, String target){
        return "SELECT "+select+" FROM "+from+" where "+field+" is "+target;
    }



}
