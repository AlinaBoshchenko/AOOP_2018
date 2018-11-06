package aoop.asteroids.Database;
import java.sql.*;

public class InsertApp {

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
            System.out.println("Connected");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param nickname
     * @param score
     */
    public void insert(String nickname, double score) {
        String sql = "INSERT INTO scores(nickname,score) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setDouble(2,score);
            System.out.println("Inserted");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        InsertApp app = new InsertApp();
        // insert three new rows
        app.insert("player1", 50);
        app.insert("player2", 20);
        app.insert("player3", 23);
    }

}
