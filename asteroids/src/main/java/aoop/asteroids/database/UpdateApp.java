package aoop.asteroids.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateApp {

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite//scoresdb.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Update data of a scores table specified by the id
     *
     * @param id
     * @param nickname nickname of the player
     * @param score score of the player
     */
    public void update(int id, String nickname, int score) {
        String sql = "UPDATE scores SET nickname = ? , "
                + "score = ? "
                + "WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, nickname);
            pstmt.setInt(2, score);
            pstmt.setInt(3, id);
            // update
            pstmt.executeUpdate();
            System.out.println("Updated");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        UpdateApp app = new UpdateApp();
        // update the warehouse with id 3
        app.update(2, "Player4", 21);
    }

}
