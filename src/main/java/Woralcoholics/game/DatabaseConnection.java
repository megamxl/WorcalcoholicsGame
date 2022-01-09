package Woralcoholics.game;

import java.sql.*;

/**
 *  @author Maxlimilian Nowak
 */

public class DatabaseConnection {

    Connection con = null;
    String insertQuery =null;
    String selectQuery =null;
    public static String[] scoresArray = {"","","","",""};
    public static boolean finishedFillingArray = false;


    /**
     * connects and inserts ito Database
     * @throws SQLException
     */
    public void insertScoreAndNameIntoDatabase() throws SQLException {
        // The query for inserting ito the database
        if (Game.playerName != null) {
             insertQuery = "INSERT INTO `Scores` (`name`, `score`) VALUES ( '" + Game.playerName + "', '" + Game.lastScore + "')";
        }
        // Method to try to connect. Database is completely unsafe at the moment but is just a free mysql Database at the moment without any privileges to create different users
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://sql4.freesqldatabase.com/sql4462776", "sql4462776", "TNqVfZS4pW");
        } catch (Exception e) {
            System.out.println("we don't have capacities for that ");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
           stmt.executeUpdate(insertQuery);
           //System.out.println("should have inserted");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }
        con.close();
    }


    /**
     * Reads back form Database
     * @throws SQLException
     */
    public void ReadFromDatabase() throws SQLException {
        selectQuery = "SELECT * FROM `Scores` ORDER BY `score` DESC limit 5";
        try {
            // Method to try to connect. Database is completely unsafe at the moment but is just a free mysql Database at the moment without any privileges to create different users
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://sql4.freesqldatabase.com:3306/sql4462776", "sql4462776", "TNqVfZS4pW");
        } catch (Exception e) {
            System.out.println("we don't have capacities for that ");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQuery);
            int i = 0;
            // now iterating over the complete result and storing it in the Array
            while (rs.next()){
                String name = rs.getString("name");
                int scores = rs.getInt("score");
                String added = name + " " + scores;
                if(i < 6){
                    scoresArray[i] = added;
                }
                i++;
            }
            // because the database QUERY takes longer than the render method a boolean is used against the race condition.
            finishedFillingArray = true;
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }
        con.close();
    }
}



