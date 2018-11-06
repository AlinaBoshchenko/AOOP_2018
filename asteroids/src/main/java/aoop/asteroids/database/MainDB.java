package aoop.asteroids.database;


import java.sql.*;

public class MainDB {
    /**
     * The system separator character.
     */
    private static String separator = System.getProperty("file.separator");

    /**
     * The default directory of the data base.
     */
    private static String url = "jdbc:sqlite:" + "asteroids" + separator +
            "src" + separator + "main" + separator + "db" + separator + "scores.db";


    /**
     * Checks if there exists a score database within the default directory
     * and attempts to create one if there is not.
     * @return false - if it is impossible to connect or create the database.
     *         true  - otherwise
     */
    public static boolean connnectDataBase() {
        String sqlQuerry = "CREATE TABLE IF NOT EXISTS scores (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	nickname text NOT NULL,\n"
                + "	score integer\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlQuerry);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Connect to the test.db database
     * @return the Connection object
     */

    private static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static int getPlayerScore(String nickName) {
        String sql = "SELECT score FROM scores WHERE nickname = '" + nickName + "'";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if(rs.next()) {
                return rs.getInt("score");
            } else {
                insert(nickName, 0);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static void insert(String nickname, int score) {
        String sql = "INSERT INTO scores(nickname,score) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setInt(2,score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updatePlayerScore(String nickname, int score) {
        String query = "UPDATE scores SET score = " + score + " WHERE nickname = '" + nickname + "'";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
