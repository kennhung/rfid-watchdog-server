package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Setting {

    public static String loadSettingString(String settingName) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM settings where name is '"+settingName+"'");

        if(query.isClosed()){
            return null;
        }
        return query.getString("val");
    }

    public static String saveSettingString(String name, String config) throws SQLException {
        if(loadSettingString(name) == null){
            SQLite.getStatement().execute("insert into settings (name, val) values ('"+name+"','"+config+"')");
        }
        else{
            SQLite.getStatement().execute("UPDATE settings SET val = '"+config+"' WHERE name = '"+name+"'");
        }
        SQLite.getConnection().commit();
        return name;
    }

    public static Map<String,String> loadAllSettingsString() throws SQLException {
        Map<String,String> settings = new HashMap<>();

        ResultSet query = SQLite.getStatement().executeQuery("select * from settings");
        if(query.isClosed()){
            return null;
        }

        while (query.next()){
            settings.put(query.getString("name"),query.getString("val"));
        }

        return settings;
    }

}
