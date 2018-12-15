package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    public int id;
    public String name;
    public String groups;
    public String uid;
    public String metadata;
    public long validate;
    public boolean enable;
    public String password;

    public User() {
        name = "";
        groups = "[]";
        id = 0;
        uid = "";
        metadata = "{}";
        validate = 0;
        enable = true;
        password = "";
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
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM users where id is '" + user.id + "' or uid is '"+ user.uid+"'");
        if (query.isClosed()) {
            // This is new user
            SQLite.getStatement().execute("INSERT INTO users (`name`, `groups`,`uid`, `metadata`, `validate`," +
                    " `enable`, `password`)" +
                    "VALUES ('" + user.name + "','" + user.groups + "','" + user.uid + "','"  + user.metadata +"','"  + user.validate+"','"  + user.enable+"','"  + user.password+ "')");
        } else {
            // Old user
            SQLite.getStatement().execute("DELETE FROM users WHERE id is " + user.id);
            SQLite.getStatement().execute("INSERT INTO users (`id`,`name`, `groups`,`uid`, `metadata`, `validate`," +
                    " `enable`, `password`)" +
                    "VALUES ('" + user.id + "','" + user.name + "','" + user.groups + "','" + user.uid + "','"
                    + user.metadata +"','"  + user.validate+"','"  + user.enable+"','"  + user.password+ "')");
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
        res.metadata = query.getString("metadata");
        res.validate = query.getLong("validate");
        res.enable = query.getBoolean("enable");
        res.password = query.getString("password");
        return res;
    }

    // User use JSONObject, User
    public static User encodeUser(JSONObject userdata) throws JSONException {
        User u = new User();
        u.id = userdata.getInt("id");
        u.uid = userdata.getString("uid");
        u.name = userdata.getString("name");
        u.metadata = userdata.getString("metadata");
        u.groups = userdata.getString("groups");
        u.validate = userdata.getLong("validate");
        u.enable = userdata.getBoolean("enable");
        u.password = userdata.getString("password");
        return u;
    }

    public static JSONObject decodeUser(User u) {
        JSONObject out = new JSONObject();
        out.put("id", u.id);
        out.put("uid", u.uid);
        out.put("name", u.name);
        out.put("metadata", u.metadata);
        out.put("groups", u.groups);
        out.put("validate",u.validate);
        out.put("enable",u.enable);
        out.put("password",u.password);
        return out;
    }
}
