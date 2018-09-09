package indi.kennhuang.rfidwatchdog.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Group {
    public int id;
    public String name;
    public String doors;

    public Group(){
        id = 0;
        name = "";
        doors = "[]";
    }

    public static Group findGroupById(int id) throws SQLException {
        ResultSet query = SQLite.statement.executeQuery("SELECT * FROM groups where id is " + id);
        if(query.isClosed()){
            return null;
        }
        return putResultToGroup(query);
    }

    private static Group putResultToGroup(ResultSet query) throws SQLException {
        Group res = new Group();
        res.id = query.getInt("id");
        res.name = query.getString("name");
        res.doors = query.getString("doors");
        return res;
    }

}
