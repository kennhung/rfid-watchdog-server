package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.util.database.SQLGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Setting {

    public static String loadSettingString(String name) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery(SQLGenerator.getSelectString("*","settings","name",name));
        if(query.isClosed()){
            return null;
        }
        return query.getString("val");
    }

    public static void saveSettingString(String name, String config) throws SQLException {
        if(loadSettingString(name) == null){
            SQLite.getStatement().execute("insert into settings (name, val) WITH ('"+name+"','"+config+"')");
        }
        else{
            SQLite.getStatement().execute("UPDATE settings SET val = '"+config+"' WHERE name = '"+name+"'");
        }
        SQLite.getConnection().commit();
    }

    public static Map<String,String> loadAllSettingsString() throws SQLException {
        Map<String,String> settings = new HashMap<>();

        ResultSet query = SQLite.getStatement().executeQuery(SQLGenerator.getSelectString("*","settings",null,null));
        if(query.isClosed()){
            return null;
        }

        while (query.next()){
            settings.put(query.getString("name"),query.getString("val"));
        }

        return settings;
    }

}
