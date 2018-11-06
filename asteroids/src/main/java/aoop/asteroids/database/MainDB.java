package aoop.asteroids.Database;


import java.sql.*;

public class MainDB {

    /**
     * Creates new table SCORES in the database
     */
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite//scoresdb.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS scores (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	nickname text NOT NULL,\n"
                + "	score integer\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbURL = "jdbc:sqlite:C://sqlite//scoresdb.db";
            Connection conn = DriverManager.getConnection(dbURL);
            if (conn != null) {
                System.out.println("Connected to the database");
                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
                conn.close();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        createNewTable();
    }

}
