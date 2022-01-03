package Woralcoholics.game;

import java.sql.*;
import java.util.Arrays;

public class DatabeseConection {

    Connection con = null;
    String insertQuery =null;
    String selectQurey =null;
    public static String[] scoresArray = {"","","","",""};
    public static boolean finishedFillingArray = false;


    public void connectToDatabase() throws SQLException {
        if (Game.playerName != null) {
             insertQuery = "INSERT INTO `Scores` (`name`, `score`) VALUES ( '" + Game.playerName + "', '" + String.valueOf(Game.lastScore) + "')";
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://sql4.freesqldatabase.com/sql4462776", "sql4462776", "TNqVfZS4pW");
            //here sonoo is database name, root is username and password

            System.out.println("should have inserted");
        } catch (Exception e) {
            System.out.println("we don't have capacities for that ");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
           stmt.executeUpdate(insertQuery);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }

        con.close();
    }


    public void ReadFromDatabse() throws SQLException {

        selectQurey = "SELECT * FROM `Scores` ORDER BY `score` DESC limit 5";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://sql4.freesqldatabase.com:3306/sql4462776", "sql4462776", "TNqVfZS4pW");
            //here sonoo is database name, root is username and password

            System.out.println("should have inserted");
        } catch (Exception e) {
            System.out.println("we don't have capacities for that ");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQurey);
            int i = 0;
            while (rs.next()){
                String name = rs.getString("name");
                int scores = rs.getInt("score");
                String added = name + " " + scores;
                if(i < 6){
                    scoresArray[i] = added;
                }
                i++;
            }
            finishedFillingArray = true;
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }

        con.close();
    }

}



