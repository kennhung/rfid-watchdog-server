package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.util.database.SQLGenerator;
import indi.kennhuang.rfidwatchdog.server.util.database.TableGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;

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
