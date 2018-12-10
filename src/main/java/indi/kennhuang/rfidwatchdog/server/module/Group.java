package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.module.permission.PermissionBlock;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Group {
    public int id;
    public String name;

    public Group() {
        id = 0;
        name = "";
    }

    public static Group findGroupById(int id) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM groups where id is " + id);
        if (query.isClosed()) {
            return null;
        }
        return putResult(query);
    }

    public static List<Group> getAllGroups() throws SQLException {
        List<Group> groupsOut = new ArrayList<Group>();
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM groups");
        if (query.isClosed()) {
            return null;
        }

        while (query.next()) {
            groupsOut.add(putResult(query));
        }

        return groupsOut;
    }

    private static Group putResult(ResultSet query) throws SQLException {
        Group res = new Group();
        res.id = query.getInt("id");
        res.name = query.getString("name");
        return res;
    }

    public static JSONObject decodeGroup(Group g){
        JSONObject out = new JSONObject();
        out.put("id",g.id);
        out.put("name",g.name);
        return out;
    }

}
