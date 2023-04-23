package com.rummy.server.app;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class Database{
    //Saving database url and login credentials
   
//    static final String DB_URL =
//            "jdbc:mysql://localhost:3306/rummydb?zeroDateTimeBehavior=CONVERT_TO_NULL";
//    static final String DB_USER = "root";
//    static final String DB_PASSWD = "#!#bookclub#!#";
    
     final String DB_URL;
     final String DB_USER;
     final String DB_PASSWD;
    
    
    public Database(String url, String user, String password){
        this.DB_URL=url;
        this.DB_USER=user;
        this.DB_PASSWD=password;
    }

    private int getPlayersNum() {
        return searchAmount("players") + 1;
    }

    //Helper method for getPlayersNum,getGamesNum
    //Searches in the table what is the highest id that exists, and returns it.
    private int searchAmount(String atable) {

        Connection connection = null;

        ResultSet resultSet = null;
        int result = -1;
        try {
            String selectString =
                    "SELECT * FROM ";
            selectString += atable;

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectRows = connection.prepareStatement(selectString);

            
            resultSet = selectRows.executeQuery();
            int currentID = 0;
            while (resultSet.next()) {

                currentID = resultSet.getInt("id");

                if (currentID > result) {
                    result = currentID;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }

    private int getGamesNum() {
        return searchAmount("games") + 1;
    }

    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);  

        System.out.println("url:");
        String url = myObj.nextLine();  
        
        System.out.println("user:");
        String user = myObj.nextLine();  
        
        System.out.println("password:");
        String password = myObj.nextLine();  
        
        Database mydb = new Database( url,  user,  password);
        //Endless loop to show user all command options
        while (true) {
            System.out.println("addplayer,playersscore,setscore,playeronline,setonline,creategame,deletegame,addplayergame or exit:");
            System.out.println(" getgameplayers or exit:");
            String choice = myObj.nextLine();  // Read user input

            if (choice.equals("addplayer")) {

                System.out.println("name:");
                String name = myObj.nextLine();  // Read user input
                System.out.println("password:");
                String pass = myObj.nextLine();  // Read user input
                System.out.println("result: " + mydb.createPlayer(name, pass));
            } else if (choice.equals("exit")) {
                break;
            } else if (choice.equals("playersscore")) {
                System.out.println("id:");
                String id = myObj.nextLine();  // Read user input
                System.out.println("score:" + mydb.getPlayerScore(Integer.parseInt(id)));
            } else if (choice.equals("setscore")) {
                System.out.println("id:");
                String id = myObj.nextLine();  // Read user input
                System.out.println("score:");
                String score = myObj.nextLine();  // Read user input
                mydb.setPlayerScore(Integer.parseInt(id), Integer.parseInt(score));
            } else if (choice.equals("playeronline")) {
                System.out.println("id:");
                String id = myObj.nextLine();  // Read user input
                System.out.println("online:" + mydb.getPlayerOnline(Integer.parseInt(id)));
            } else if (choice.equals("setonline")) {
                System.out.println("id:");
                String id = myObj.nextLine();  // Read user input
                System.out.println("online:");
                String online = myObj.nextLine();  // Read user input
                mydb.setPlayerOnline(Integer.parseInt(id), Integer.parseInt(online));
            } else if (choice.equals("creategame")) {
                System.out.println("playerID:");
                String id = myObj.nextLine();  // Read user input

                System.out.println("game name:");
                String gameName = myObj.nextLine();  // Read user input

                System.out.println("result: " + mydb.createGame(Integer.parseInt(id), gameName));
            } else if (choice.equals("deletegame")) {
                System.out.println("gameID:");
                String id = myObj.nextLine();  // Read user input

                mydb.deleteGame(Integer.parseInt(id));
            } else if (choice.equals("addplayergame")) {
                System.out.println("gameID:");
                String gameID = myObj.nextLine();  // Read user input

                System.out.println("playerID:");
                String playerID = myObj.nextLine();  // Read user input
                mydb.addPlayerGame(Integer.parseInt(playerID), Integer.parseInt(gameID));
            } else if (choice.equals("getgameplayers")) {
                System.out.println("gameID:");
                String gameID = myObj.nextLine();  // Read user input

                int players[] = mydb.getGamePlayers(Integer.parseInt(gameID));
                System.out.printf("player1:%d, player2:%d\n", players[0], players[1]);
            } else if (choice.equals("getwinner")) {
                System.out.println("gameID:");
                String gameID = myObj.nextLine();  // Read user input

                int player = mydb.getWinner(Integer.parseInt(gameID));
                System.out.printf("winner:%d\n", player);
            } else if (choice.equals("setwinner")) {
                System.out.println("gameID:");
                String gameID = myObj.nextLine();  // Read user input

                System.out.println("playerID:");
                String playerID = myObj.nextLine();  // Read user input
                mydb.setWinner(Integer.parseInt(gameID), Integer.parseInt(playerID));
            } else if (choice.equals("setgameactive")) {
                System.out.println("gameID:");
                String gameID = myObj.nextLine();  // Read user input

                System.out.println("active:");
                String active = myObj.nextLine();  // Read user input
                mydb.setGameActive(Integer.parseInt(gameID), Integer.parseInt(active));
            } else if (choice.equals("getplayername")) {
                System.out.println("playerID:");
                String playerID = myObj.nextLine();  // Read user input
                System.out.println(mydb.getPlayerName(Integer.parseInt(playerID)));
            }
        }//end of while true
    }//end of main

    public boolean checkPassword(String name, String enteredPassword) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection
                    (DB_URL, DB_USER, DB_PASSWD);
            statement = connection.createStatement();

            //Check if this player exists
            String selectString =
                    "SELECT * FROM players where name = ? ";

            PreparedStatement selectPlayer = connection.prepareStatement(selectString);
            selectPlayer.setString(1, name);
            resultSet = selectPlayer.executeQuery();
            boolean foundPlayer = false;
            while (resultSet.next()) {

                foundPlayer = true;
            }

            if (!foundPlayer) {
                return false;
            }

            String hash = "", salt = "";

            resultSet = selectPlayer.executeQuery();

            while (resultSet.next()) {

                hash = resultSet.getString("password");
                salt = resultSet.getString("salt");
            }

            return passwordManager.checkPass(enteredPassword, salt, hash);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }

        }//end of finally

        return false;
    }

    //return -1 if error in creating
    public int createPlayer(String name, String password) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int newId = getPlayersNum();

        try {
            connection = DriverManager.getConnection
                    (DB_URL, DB_USER, DB_PASSWD);
            statement = connection.createStatement();

            //Check if this player already exists
            String selectString =
                    "SELECT * FROM players where name = ? ";

            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setString(1, name);

            resultSet = selectPlayer.executeQuery();
            boolean foundPlayer = false;
            while (resultSet.next()) {

                foundPlayer = true;
            }

            if (foundPlayer) {
                return -1;
            }

            //Otherwise, add new player

            String result[] = new String[2];

            try {
                result = passwordManager.storePass(password);
            } catch (Exception ex) {
                System.out.println("error in creating user due to password");
                return -1;
            }
            resultSet = statement.executeQuery
                    ("SELECT * FROM players");

            Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = ps.executeQuery("SELECT * FROM players");

            uprs.moveToInsertRow();
            uprs.updateString("name", name);
            uprs.updateString("password", result[0]);
            uprs.updateString("salt", result[1]);
            uprs.updateInt("id", newId);
            uprs.updateInt("online", 0);
            uprs.updateInt("generalScore", 0);
            uprs.insertRow();
            uprs.beforeFirst();

            

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }

        }//end of finally

        return 1;
    }

    public int getPlayerScore(int id) {
        Connection connection = null;

        ResultSet resultSet = null;
        int result = -1;
        try {
            String selectString =
                    "SELECT * FROM players where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet = selectPlayer.executeQuery();
            while (resultSet.next()) {

                result = resultSet.getInt("generalScore");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }

        }
        return result;
    }

    public int setPlayerScore(int id, int score) {
        Connection connection = null;

        String updateString =
                "update players set generalScore = ? where id = ?";

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement updatePlayer = connection.prepareStatement(updateString);

            updatePlayer.setInt(1, score);
            updatePlayer.setInt(2, id);
            updatePlayer.executeUpdate();
            System.out.println("updated score");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }

        }
        return 1;
    }

    public int getPlayerOnline(int id) {
        Connection connection = null;

        ResultSet resultSet = null;
        int result = -1;
        try {
            String selectString =
                    "SELECT * FROM players where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet = selectPlayer.executeQuery();
            while (resultSet.next()) {

                result = resultSet.getInt("online");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }

        }
        return result;

    }

    //0 - offline
    //1 - online
    public int setPlayerOnline(int id, int online) {
        Connection connection = null;

        String updateString =
                "update players set online = ? where id = ?";

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement updatePlayer = connection.prepareStatement(updateString);

            updatePlayer.setInt(1, online);
            updatePlayer.setInt(2, id);
            updatePlayer.executeUpdate();
            //System.out.println("updated online");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }

        }
        return 1;
    }

    public int getGameID(String name) {
        Connection connection = null;

        ResultSet resultSet = null;
        int result = -1;
        try {
            String selectString =
                    "SELECT * FROM games where name = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectGame = connection.prepareStatement(selectString);

            selectGame.setString(1, name);
            resultSet = selectGame.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }

        }

        return result;
    }


    public int createGame(int playerID, String gameName) {
        Connection connection = null;

        int newId = getGamesNum();
        try {
            connection = DriverManager.getConnection
                    (DB_URL, DB_USER, DB_PASSWD);

            //Check if game with this player already exists
            String selectString =
                    "SELECT * FROM games where player1 = ? OR player2 = ?";


            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            ResultSet resultSet = null;
            selectPlayer.setInt(1, playerID);
            selectPlayer.setInt(2, playerID);
            resultSet = selectPlayer.executeQuery();
            boolean foundPlayer = false;

            while (resultSet.next()) {

                foundPlayer = true;
                System.out.println("found game with player, not creating game");
                return -1;
            }

            //Check if player exists
            selectString =
                    "SELECT * FROM players where id = ?";

            selectPlayer = connection.prepareStatement(selectString);

            resultSet = null;
            selectPlayer.setInt(1, playerID);

            resultSet = selectPlayer.executeQuery();
            foundPlayer = false;
            while (resultSet.next()) {
                foundPlayer = true;
            }

            if (!foundPlayer) {
                System.out.println("Did not found player for creating game");
                return -1;
            }

            Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //create the game
            ResultSet uprs = ps.executeQuery("SELECT * FROM games");
            uprs.moveToInsertRow();
            uprs.updateInt("active", 0);
            uprs.updateInt("id", newId);
            uprs.updateInt("player1", playerID);
            uprs.updateString("name", gameName);
            uprs.updateInt("winner", -1);
            uprs.insertRow();
            uprs.beforeFirst();

            

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }
        }//end of finally

        return 1;
    }

    public int deleteGame(int id) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection
                    (DB_URL, DB_USER, DB_PASSWD);

            //Delete game
            String deleteString = "DELETE FROM games where id = ?";
            PreparedStatement deleteGame = connection.prepareStatement(deleteString);
            deleteGame.setInt(1, id);
            deleteGame.executeUpdate();

            System.out.println("Deleted game...");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }//end of finally

        return 1;
    }

    public int addPlayerGame(int playerID, int gameID) {
        Connection connection = null;

        String updateString =
                "update games set player2 = ? where id = ?";

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement updatePlayer = connection.prepareStatement(updateString);

            updatePlayer.setInt(1, playerID);
            updatePlayer.setInt(2, gameID);
            updatePlayer.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }
        return 1;
    }

    //0 - not started yet
    //1 - now playing
    //2 - finished, can look at winner
    public int setGameActive(int id, int active) {
        Connection connection = null;

        String updateString =
                "update games set active = ? where id = ?";

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement updatePlayer = connection.prepareStatement(updateString);

            updatePlayer.setInt(1, active);
            updatePlayer.setInt(2, id);
            updatePlayer.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }
        return 1;
    }
    
     //0 - not started yet
    //1 - now playing
    //2 - finished, can look at winner
    public int getGameActive(int id) {
        Connection connection = null;
        ResultSet resultSet = null;

        int result = -1;
        try {

            String selectString =
                    "SELECT * FROM games where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectGame = connection.prepareStatement(selectString);

            selectGame.setInt(1, id);
            resultSet = selectGame.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt("active");

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }
    

    public int[] getGamePlayers(int id) {
        Connection connection = null;
        ResultSet resultSet = null;

        int players[] = new int[2];
        try {
            String selectString =
                    "SELECT * FROM games where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet = selectPlayer.executeQuery();
            while (resultSet.next()) {
                players[0] = resultSet.getInt("player1");
                players[1] = resultSet.getInt("player2");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
            }
        }

        return players;
    }

    //Only return winner if game is finised, meaning active =2
    //winner is id of winning player
    //If problem with getting winner, return -1
    //If games still not finished, return -2
    public int getWinner(int id) {
        Connection connection = null;
        ResultSet resultSet = null;

        int result = -1;
        try {

            String selectString =
                    "SELECT * FROM games where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet = selectPlayer.executeQuery();
            while (resultSet.next()) {
                int active = resultSet.getInt("active");
                if (active == 2) {
                    result = resultSet.getInt("winner");

                } else {
                    result = -2;

                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }

    //if problem, return -1
    public int getID(String name) {
        Connection connection = null;
        ResultSet resultSet = null;

        int result = -1;
        try {

            String selectString =
                    "SELECT * FROM players where name = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setString(1, name);
            resultSet = selectPlayer.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt("id");

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }

    public int setWinner(int gameID, int playerID) {
        Connection connection = null;

        String updateString =
                "update games set winner = ? where id = ?";

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement updatePlayer = connection.prepareStatement(updateString);

            updatePlayer.setInt(1, playerID);
            updatePlayer.setInt(2, gameID);
            updatePlayer.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return 1;
    }

    public String getPlayerName(int ID) {
        Connection connection = null;
        ResultSet resultSet = null;

        String result = "null";

        try {
            String selectString =
                    "SELECT * FROM players where id = ?";

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, ID);
            resultSet = selectPlayer.executeQuery();

            while (resultSet.next()) {
                result = resultSet.getString("name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();

                if (connection != null) connection.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }
}

