package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
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

    public static Group findGroupByName(String name) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM groups where name is '" + name +"'");
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
    // get


    public static void saveGroup(Group g) throws SQLException {
        if (findGroupById(g.id) == null) {
            // new group
            SQLite.getStatement().execute("insert into groups (`name` ) values ('" + g.name + "' )");
        } else {
            // old group
            SQLite.getStatement().execute("delete from groups where id is " + g.id);
            SQLite.getStatement().execute("insert into groups (`id`, `name` ) values (" + g.id + ",'" + g.name + "')");
        }
        SQLite.getConnection().commit();
    }

    public static int deleteGroup(Group group) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM groups where id is " + group.id);
        if (query.isClosed()) {
            return 1;
        } else {
            SQLite.getStatement().execute("DELETE FROM groups WHERE id is " + group.id);
        }
        SQLite.getConnection().commit();
        return 0;
    }

    private static Group putResult(ResultSet query) throws SQLException {
        Group res = new Group();
        res.id = query.getInt("id");
        res.name = query.getString("name");
        return res;
    }

    public static Group encodeGroup(JSONObject json){
        Group g = new Group();
        g.id = json.getInt("id");
        g.name = json.getString("name");
        return g;
    }

    public static JSONObject decodeGroup(Group g) {
        JSONObject out = new JSONObject();
        out.put("id", g.id);
        out.put("name", g.name);
        return out;
    }

}
