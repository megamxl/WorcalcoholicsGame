package Woralcoholics.game;

import java.sql.*;

/**
 * @author Maxlimilian Nowak
 */

public class DatabaseConnection {

    // Variables for connecting and database requests
    Connection con = null;
    String insertQuery = null;
    String selectQuery = null;
    public static String[] scoresArray = {"", "", "", "", ""};
    public static boolean finishedFillingArray = false;

    /**
     * connects and inserts ito Database
     *
     * @throws SQLException
     */
    public void insertScoreAndNameIntoDatabase() throws SQLException {
        // The query for inserting ito the database
        if (Game.playerName != null && !Game.playerName.equals(" ")) { // gets only executed if player Input valid
            insertQuery = "INSERT INTO `Scores` (`name`, `score`) VALUES ( '" + Game.playerName + "', '" + Game.lastScore + "')";

            // Method to try to connect. Database is completely unsafe at the moment but is just a free mysql Database at the moment without any privileges to create different users
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");  // get the mySqlJavaDriver
                con = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11465142", "sql11465142", "3EUdzq6kPX"); // login Credentials for Database
            } catch (Exception e) {                         // catches connection Errors
                System.out.println("Can't connect to Database");
                System.out.println(e);
            }
            try (Statement stmt = con.createStatement()) {  // tries executing the Statement
                stmt.executeUpdate(insertQuery);
                //System.out.println("should have inserted");
            } catch (SQLException e) {
                System.out.println(e);
                System.out.println("Can't insert into Database");
            }
            con.close();                                    // always close the connection when you're done working with Databases
        }
    }

    /**
     * Reads back form Database
     *
     * @throws SQLException
     */
    public void ReadFromDatabase() throws SQLException {
        selectQuery = "SELECT * FROM `Scores` ORDER BY `score` DESC limit 5"; // sql query to get the 5 best Scores from the table
        try {
            // Method to try to connect. Database is completely unsafe at the moment but is just a free mysql Database at the moment without any privileges to create different users
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://sql11.freesqldatabase.com/sql11465142", "sql11465142", "3EUdzq6kPX");
        } catch (Exception e) {
            System.out.println("Can't connect to Database");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQuery);  // get the Result in a result set
            int i = 0;                                      // counter variable for the Array
            // now iterating over the complete result and storing it in the Array
            while (rs.next()) {
                String name = rs.getString("name"); // getting Name
                int scores = rs.getInt("score");    // getting Score
                String added = name + " " + scores;           //  adding both to one String
                if (i < 6) {                                  // check if in bounds
                    scoresArray[i] = added;                   // fill the array
                }
                i++;
            }
            finishedFillingArray = true;                      // because the database QUERY takes longer than the render method a boolean is used against the race condition.

        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }
        con.close();
    }
}