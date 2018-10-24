package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class User {
    public int id;
    public String name;
    public String groups;
    public String uid;
    public List<DoorPermission> doors;
    public String metadata;

    public User() {
        name = "";
        groups = "[]";
        id = 0;
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

    public static List<User> getAllUsers() throws SQLException {
        List<User> usersOut = new ArrayList<User>();
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users");
        if (query.isClosed()) {
            return null;
        }

        while (query.next()) {
            usersOut.add(putResult(query));
        }

        return usersOut;
    }

    public static int saveUser(User user) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where id is " + user.id);

        if (query.isClosed()) {
            // This is new user
            SQLite.getStatement().execute("INSERT INTO users (`name`, `groups`,`uid`, `doors`, `metadata`)" +
                    "VALUES ('" + user.name + "','" + user.groups + "','" + user.uid + "','" + decodeDoorPermissions(user.doors) + "','" + user.metadata + "')");
        } else {
            // Old user
            SQLite.getStatement().execute("DELETE FROM users WHERE id is " + user.id);
            SQLite.getStatement().execute("INSERT INTO users ( `id`, `name`, `groups`,`uid`, `doors`, `metadata`)" +
                    "VALUES (" + user.id + ",'" + user.name + "','" + user.groups + "','" + user.uid + "','" + decodeDoorPermissions(user.doors) + "','" + user.metadata + "')");
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
        res.name = query.getString("name");
        res.groups = query.getString("groups");
        res.uid = query.getString("uid");
        res.doors = encodeDoorPermissions(query.getString("doors"));
        res.metadata = query.getString("metadata");
        return res;
    }


    // DoorPermissions use String and List<DoorPermission>
    public static List<DoorPermission> encodeDoorPermissions(String in) {
        List<DoorPermission> doors = new ArrayList<DoorPermission>();
        JSONArray doorsJSON = new JSONArray(in);
        for (Object o : doorsJSON) {
            if (o instanceof JSONObject) {
                doors.add(DoorPermission.encodeDoorPermission((JSONObject) o));
            }
        }
        return doors;
    }

    public static String decodeDoorPermissions(List<DoorPermission> in) {
        Iterator i = in.iterator();
        JSONArray doors = new JSONArray();
        while (i.hasNext()){
            DoorPermission door = (DoorPermission) i.next();
            doors.put(door.getJSONObject());
        }
        return doors.toString();
    }

    public String getDoorPermissionsString(){
        return decodeDoorPermissions(this.doors);
    }

    // User use JSONObject, User
    public static User encodeUser(JSONObject userdata) throws JSONException {
        User u = new User();
        u.id = userdata.getInt("id");
        u.uid = userdata.getString("uid");
        u.name = userdata.getString("name");
        u.metadata = userdata.getString("metadata");
        u.groups = userdata.getString("groups");
        u.doors = encodeDoorPermissions(userdata.getString("doors"));
        return u;
    }

    public static JSONObject decodeUser(User u) {
        JSONObject out = new JSONObject();
        out.put("id", u.id);
        out.put("uid", u.uid);
        out.put("name", u.name);
        out.put("metadata", u.metadata);
        out.put("groups", u.groups);
        out.put("doors", decodeDoorPermissions(u.doors));
        return out;
    }
}
