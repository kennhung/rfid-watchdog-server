package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.util.database.SQLGenerator;
import indi.kennhuang.rfidwatchdog.server.util.database.TableGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Setting {
    public int id;
    public String name;
    public String value;

    public Setting(){
        id = 0;
        name = "";
        value = "";
    }


    public static Setting getFromId(int id) throws SQLException {
        String queryString = SQLGenerator.getSelectString("*","settings","id", String.valueOf(id));
        ResultSet query = SQLite.getStatement().executeQuery(queryString);

        if (query.isClosed()) {
            return null;
        }
        return putResult(query);
    }

    public static List<Setting> getAll() throws SQLException {
        List<Setting> settingsOut = new ArrayList<Setting>();
        ResultSet query = SQLite.getStatement().executeQuery(SQLGenerator.getSelectString("*","settings",null,null));

        if(query.isClosed()){
            return null;
        }

        while (query.next()) {
            settingsOut.add(putResult(query));
        }

        return settingsOut;
    }

    public static void save(Setting setting) throws SQLException {
        if(getFromId(setting.id) == null){
            // new setting
            SQLite.getStatement().execute("insert into settings (`name`, `value` ) values ('" + setting.name + "', '"+setting.value+"' )");
        } else {
            // old setting
            SQLite.getStatement().execute("delete from settings where id is " + setting.id);
            SQLite.getStatement().execute("insert into settings (`id`, `name`, `value` ) values (" + setting.id + ",'" + setting.name + "','" + setting.value + "')");
        }
    }

    private static Setting putResult(ResultSet res) throws SQLException {
        Setting setting = new Setting();

        setting.id = res.getInt("id");
        setting.name = res.getString("name");
        setting.value = res.getString("value");

        return setting;
    }

    public static String getNewTableString(){
        TableGenerator tg = new TableGenerator("settings");

        try {
            tg.addInt("id");
            tg.addText("name");
            tg.addText("value");
            tg.setPk("id",true);
        } catch (TableGenerator.FieldConfigException e) {
            e.printStackTrace();
            return null;
        }

        return tg.getGenerateString();
    }
}
