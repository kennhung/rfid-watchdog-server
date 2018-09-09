package indi.kennhuang.rfidwatchdog.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public int id;
    public String name;
    public int studentId;
    public String groups;
    public String uid;
    public String doors;

    public User() {
        name = "";
        groups = "[]";
        id = 0;
        studentId = 0;
        uid = "";
        doors = "[]";
    }

    public static User findUserById(int id) throws SQLException {
        ResultSet query = SQLite.statement.executeQuery("SELECT * FROM users where id is " + id);
        if (query.isClosed()) {
            return null;
        }
        return putResultToUser(query);
    }

    public static User findUserByUid(String uid) throws SQLException {
        ResultSet query = SQLite.statement.executeQuery("SELECT * FROM users where uid is '" + uid+"'");
        if (query.isClosed()) {
            return null;
        }
        return putResultToUser(query);
    }

    public static int saveUserToDB(User user) throws SQLException {
        if(user.id == 0){
            return 1;
        }
        ResultSet query = SQLite.statement.executeQuery("SELECT * FROM users where id is " + user.id);
        if (query.isClosed()) {
            // This is new user
            SQLite.statement.execute("INSERT INTO users ( `id`, `name`, `studentId`, `groups`,`uid`, `doors`)" +
                    "VALUES (" + user.id + ",'" + user.name + "'," + user.studentId + ",'" + user.groups + "','" + user.uid + "','" + user.doors+"')");
        } else {
            // Old user
            SQLite.statement.execute("DELETE FROM users WHERE id is " + user.id);
            SQLite.statement.execute("INSERT INTO users ( `id`, `name`, `studentId`, `groups`,`uid`, `doors`)" +
                    "VALUES (" + user.id + ",'" + user.name + "'," + user.studentId + ",'" + user.groups + "','" + user.uid + "','" + user.doors+"')");
        }
        SQLite.connection.commit();
        return 0;
    }

    public static int deleteUser(User user) throws SQLException {
        ResultSet query = SQLite.statement.executeQuery("SELECT * FROM users where id is " + user.id);
        if (query.isClosed()) {
            return 1;
        }
        else {
            SQLite.statement.execute("DELETE FROM users WHERE id is " + user.id);
        }
        SQLite.connection.commit();
        return 0;
    }

    private static User putResultToUser(ResultSet query) throws SQLException {
        User res = new User();
        res.id = query.getInt("id");
        res.studentId = query.getInt("studentId");
        res.name = query.getString("name");
        res.groups = query.getString("groups");
        res.uid = query.getString("uid");
        res.doors = query.getString("doors");
        return res;
    }
}
