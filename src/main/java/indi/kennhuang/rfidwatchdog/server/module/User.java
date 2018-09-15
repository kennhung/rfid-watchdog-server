package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    public int id;
    public String name;
    public int studentId;
    public String groups;
    public String uid;
    public List<DoorPermission> doors;
    public String metadata;

    public User() {
        name = "";
        groups = "[]";
        id = 0;
        studentId = 0;
        uid = "";
        doors = new ArrayList<DoorPermission>();
        metadata = "{}";
    }

    public static User getUserById(int id) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where id is " + id);
        if (query.isClosed()) {
            return null;
        }
        return putResult(query);
    }

    public static User getUserByUid(String uid) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where uid is '" + uid + "'");
        if (query.isClosed()) {
            return null;
        }
        return putResult(query);
    }

    public static int saveUser(User user) throws SQLException {
        if (user.id == 0) {
            return 1;
        }
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where id is " + user.id);
        if (query.isClosed()) {
            // This is new user
            SQLite.getStatement().execute("INSERT INTO users ( `id`, `name`, `studentId`, `groups`,`uid`, `doors`)" +
                    "VALUES (" + user.id + ",'" + user.name + "'," + user.studentId + ",'" + user.groups + "','" + user.uid + "','" + user.doors + "')");
        } else {
            // Old user
            SQLite.getStatement().execute("DELETE FROM users WHERE id is " + user.id);
            SQLite.getStatement().execute("INSERT INTO users ( `id`, `name`, `studentId`, `groups`,`uid`, `doors`)" +
                    "VALUES (" + user.id + ",'" + user.name + "'," + user.studentId + ",'" + user.groups + "','" + user.uid + "','" + user.doors + "')");
        }
        SQLite.getConnection().commit();
        return 0;
    }

    public static int deleteUser(User user) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where id is " + user.id);
        if (query.isClosed()) {
            return 1;
        } else {
            SQLite.getStatement().execute("DELETE FROM users WHERE id is " + user.id);
        }
        SQLite.getConnection().commit();
        return 0;
    }

    private static User putResult(ResultSet query) throws SQLException {
        User res = new User();
        res.id = query.getInt("id");
        res.studentId = query.getInt("studentId");
        res.name = query.getString("name");
        res.groups = query.getString("groups");
        res.uid = query.getString("uid");
        JSONArray doorsArr = new JSONArray(query.getString("doors"));
        for (int i = 0; i < doorsArr.length(); i++) {
            JSONObject door = doorsArr.getJSONObject(i);
            DoorPermission permission = new DoorPermission(door.getInt("doorId"), door.getBoolean("open"),door.getLong("validDate") );
            res.doors.add(permission);
        }
        return res;
    }
}
