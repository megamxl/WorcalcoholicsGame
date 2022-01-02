package Woralcoholics.game;

import java.sql.*;

public class DatabeseConection {

    Connection con = null;
    String query =null;

    public void connectToDatabase() throws SQLException {
        if (Game.playerName != null) {
             query = "INSERT INTO `Scores` (`ID`, `name`, `score`) VALUES ('1', '" + Game.playerName + "', '" + String.valueOf(Game.lastScore) + "')";
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://sql4.freesqldatabase.com:3306/sql4462776", "sql4462776", "lol");
            //here sonoo is database name, root is username and password

            System.out.println("should have inserted");
        } catch (Exception e) {
            System.out.println("we don't have capacities for that ");
            System.out.println(e);
        }
        try (Statement stmt = con.createStatement()) {
           stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("not excecuted");
        }

        con.close();
    }
}



