package aoop.asteroids.database;

import java.sql.*;
import java.util.logging.Logger;

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
     * The logger of this class.
     */
    private static Logger logger = Logger.getLogger(MainDB.class.getName());

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
            logger.fine("Table missing. Successfully created new.");
        } catch (SQLException e) {
            logger.severe("Could not connect to an existing database or create a new one. Reason: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Connect to the test.db database
     * @return the Connection object
     */

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.severe("Could not establish connection with the data base. Reason: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Gets the score of a player stored in the database. If there is no field in the data base. Create a new one with the score 0, and return 0.
     * @param nickName - the nickname of the player.
     * @return - the score of the player in the data base if any, otherwise 0.
     */
    public static int getPlayerScore(String nickName) {
        String sql = "SELECT score FROM scores WHERE nickname = '" + nickName + "'";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if(rs.next()) {
                return rs.getInt("score");
            } else {
                insert(nickName);
            }
        } catch (SQLException e) {
            logger.severe("Could not retrieve player score from the data base. Reason: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Inserts a new field with score 0 in the database
     */
    private static void insert(String nickname) {
        String sql = "INSERT INTO scores(nickname,score) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setInt(2, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Could not insert a new field in the database. Reason: " + e.getMessage());
        }
    }

    /**
     * Updates a player score in the data base.
     * @param nickname - the nickname of the player
     * @param score - the new score
     */
    public static void updatePlayerScore(String nickname, int score) {
        String query = "UPDATE scores SET score = " + score + " WHERE nickname = '" + nickname + "'";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Could not update a player score in the database. Reason: " + e.getMessage());
        }
    }

}
