package aoop.asteroids.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Class responsible for establishing a connection to the database **/
public class ConnectBase {

    private Connection connection;
    private String path;

    /**path of the database **/
    public ConnectBase(String path) {
        this.path = path;
    }

    /**Return the connection **/
    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}

