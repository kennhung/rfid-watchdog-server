package indi.kennhuang.rfidwatchdog.server.db;

import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {
    protected static Connection connection = null;
    protected static Statement statement = null;
    private static WatchDogLogger logger = new WatchDogLogger(LogType.DB);

    public static void openDatabase(String url) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        try {
            // create a database connection
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
            logger.info("Opened database successfully");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users " +
                    "( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` string, `groups` string, `uid` TEXT,  " +
                    "`metadata` TEXT , `validate` long, `enable` boolean, `password` string)");
            // Users
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS groups " +
                    "( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT, `doors` TEXT )");
            // Groups
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS doors " +
                    "( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT, `auth_token` TEXT )");
            /// Doors
            connection.commit();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void closeDatabase(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Statement getStatement(){
        return statement;
    }

    public static Connection getConnection(){
        return connection;
    }
}
