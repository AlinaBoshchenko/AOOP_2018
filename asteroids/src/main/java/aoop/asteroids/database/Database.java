package aoop.asteroids.Database;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.persistence.Entity;

/** Class allows to implement and exploit the SQLite database **/
@Entity
public class Database implements Serializable {

    /**the file separator**/
    private String separator = System.getProperty("file.separator");
    /** The path of the database**/
    private String databasePath = "db" + separator + "Players.sqlite";

    private ArrayList<String> nicknames;
    private ArrayList<Integer> scores;

    /**The connection from the actual database**/
    private ConnectBase connectionActual;
    /**local connection**/
    private Connection connectionLocal;



    /** get id's from the database**/
    private String getId = "select id from Data";
    /** Update the name and score of a person in the database**/
    private String updateInfo;
    /**loads from the file to the lists**/
    private String loadToLists = "select id,name,score from Data";
    /** saves new information to the database**/
    private String saveToDatabase = "insert into Data (id,name,score) values (?,?,?)";




    /**Initialize the lists and insert the data from the base**/
    public Database(){
        connectionActual = new ConnectBase(databasePath);
        this.connectionLocal = connectionActual.getConnection();
        nicknames = new ArrayList<String>();
        scores = new ArrayList<Integer>();
        loadContent();
    }

    /**load the content of the database**/
    private void loadContent() {
        try {
            PreparedStatement statement = this.connectionLocal.prepareStatement(loadToLists);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                scores.add(result.getInt(3));
                nicknames.add(result.getString(2));
            }
            statement.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Update or create a player's score**/
    public void updateScores(String name, int score){
        int current = 0;
        for(String name1 : nicknames){
            if(name1.equals(name) && score > scores.get(current)){
                scores.set(current, score);
                updateDataBase(name, score);
                return;
            }
            current++;
        }
        addToDatabase(name, score);
    }

    /** Update the database with a given username and score**/
    private void updateDataBase(String name, int score){
        try {
            updateInfo = "Update Data score ='" + score + "' ,name = '" + name + "'";
            PreparedStatement statement = this.connectionLocal.prepareStatement(updateInfo);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**add the actual player to the database**/
    private void add(String name, int score){
        try {
            PreparedStatement statement = this.connectionLocal.prepareStatement(saveToDatabase);
            statement.setString(1, Integer.toString(getNumberOfIds()));
            statement.setString(2, name);
            statement.setInt(3, score);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Add new data to the database**/
    public void addToDatabase(String name, int score){
        for(String name1 : nicknames){
            if(name1.equals(name)){
                return;
            }
        }
        nicknames.add(name);
        scores.add(score);
        add(name, score);
    }


    /** get the score by the player's name**/
    public int getPlayersScore(String name){
        int current = 0;
        for(String name1 : nicknames){
            if(name1.equals(name)){
                return scores.get(current);
            }
            current++;
        }
        return 0;
    }

    /** Get the amount of id's in the database and return the amount +1**/
    private int getNumberOfIds() {
        int value = 0;
        int max = 0;
        try {
            PreparedStatement statement = this.connectionLocal.prepareStatement(getId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                value = result.getInt("id");
                if (value > max) {
                    max = value;
                }
            }
            result.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max + 1;
    }

    public ArrayList<String> getNames(){
        return this.nicknames;
    }
    public ArrayList<Integer> getScores(){
        return this.scores;
    }
}

