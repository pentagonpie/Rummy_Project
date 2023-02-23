package com.rummy.server.app;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;

class deck{
   String cards = "1";

   public String getCards(){
   
       return cards;
   }
   
   }

class player{
    
    
}
public class Database {
    //Saving database url and login credentials
    //"jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=CONVERT_TO_NULL";
   static final String DB_URL =
      "jdbc:mysql://localhost:3306/rummydb?zeroDateTimeBehavior=CONVERT_TO_NULL";
   static final String DB_USER = "root";
   static final String DB_PASSWD = "#!#bookclub#!#";

   //global counters to keep how many players and games were created.
   //Used for picking new id when creating players or games.
   private static int playersCreated = 0;
   private static int gamesCreated = 0;
   
   private static int getPlayersNum(){
       return playersCreated;
   }
   
    private static void addPlayersNum(){
     playersCreated++;
   }
   
    private static int getGamesNum(){
       return gamesCreated;
   }
   
    private static void addGamesNum(){
     gamesCreated++;
   }
    
    
   public static void main(String[] args){
      
      Scanner myObj = new Scanner(System.in);  // Create a Scanner object
      
      //Endless loop to show user all command options
      while(true){
          System.out.println("add,playersscore,setscore,playeronline,setonline or exit:");

      String choice = myObj.nextLine();  // Read user input
      
      if(choice.equals("add")){
        
        
        System.out.println("name:");
        String name = myObj.nextLine();  // Read user input
        createPlayer( name);
      }
      
      else if(choice.equals("exit")){
        break;
      }
      
      else if(choice.equals("playersscore")){
        System.out.println("id:");
        String id = myObj.nextLine();  // Read user input
        System.out.println("score:" + getPlayerScore(  Integer.parseInt(id)));
      }
      
      else if(choice.equals("setscore")){
        System.out.println("id:");
        String id = myObj.nextLine();  // Read user input
        System.out.println("score:");
        String score = myObj.nextLine();  // Read user input
        setPlayerScore(Integer.parseInt(id),Integer.parseInt(score));
      }
      
      else if(choice.equals("playeronline")){
        System.out.println("id:");
        String id = myObj.nextLine();  // Read user input
        System.out.println("online:" + getPlayerOnline(  Integer.parseInt(id)));
      }
      
      else if(choice.equals("setonline")){
        System.out.println("id:");
        String id = myObj.nextLine();  // Read user input
        System.out.println("online:");
        String online = myObj.nextLine();  // Read user input
        setPlayerOnline(Integer.parseInt(id),Integer.parseInt(online));
      }

      else if(choice.equals("creategame")){
        System.out.println("playerID:");
        String id = myObj.nextLine();  // Read user input

        createGame(Integer.parseInt(id));
      }
      
      
      
      
      }//end of while true
     
   }//end of main
   
   public static int createPlayer(String name){   
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int newId = getPlayersNum()+1;

        try{
            connection=DriverManager.getConnection
               (DB_URL,DB_USER,DB_PASSWD);
            statement=connection.createStatement();


            resultSet=statement.executeQuery
               ("SELECT * FROM players");
            

            Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = ps.executeQuery("SELECT * FROM players");

            uprs.moveToInsertRow();
            uprs.updateString("name", name);
            uprs.updateInt("id", newId);
            uprs.updateInt("online", 1);
            uprs.updateInt("generalScore", 0);
            uprs.insertRow();
            uprs.beforeFirst();


            System.out.println("Updated table players...");
            addPlayersNum();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
          try{
              if(resultSet != null) resultSet.close();
              if(statement != null)     statement.close();
              if(connection != null) connection.close();
          }catch(SQLException e){}

        }//end of finally



    return -1;
   }
   
   public static int getPlayerScore(int id){
        Connection connection = null;
        
        ResultSet resultSet = null;
        int result = -1;
        try{
            
            
            String selectString =
            "SELECT * FROM players where id = ?";

            connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
                    PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet=selectPlayer.executeQuery();
            while(resultSet.next()){
               
               result = resultSet.getInt("generalScore");


         }

      }catch(SQLException e){
        System.out.println(e.getMessage());

      }finally{
        try{
            if(resultSet != null) resultSet.close();
            
            if(connection != null) connection.close();
        }catch(SQLException ex){}

        } 
        return result;
   }
   
    public static int setPlayerScore(int id, int score){
        Connection connection = null;

        String updateString =
        "update players set generalScore = ? where id = ?";

        try {connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
                PreparedStatement updatePlayer = connection.prepareStatement(updateString);
         
        updatePlayer.setInt(1, score);
        updatePlayer.setInt(2, id);
        updatePlayer.executeUpdate();
        System.out.println("updated score");

        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }finally{
        try{    
            if(connection != null) connection.close();
        }catch(SQLException ex){}

      }
     return 1;
   } 
   
   public static int getPlayerOnline(int id){
       Connection connection = null;
        
        ResultSet resultSet = null;
        int result = -1;
        try{
            
            
            String selectString =
            "SELECT * FROM players where id = ?";

            connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
                    PreparedStatement selectPlayer = connection.prepareStatement(selectString);

            selectPlayer.setInt(1, id);
            resultSet=selectPlayer.executeQuery();
            while(resultSet.next()){
               
               result = resultSet.getInt("online");


         }

      }catch(SQLException e){
        System.out.println(e.getMessage());

      }finally{
        try{
            if(resultSet != null) resultSet.close();
            
            if(connection != null) connection.close();
        }catch(SQLException ex){}

        } 
        return result;

   }
   
   public static int setPlayerOnline(int id, int online){
        Connection connection = null;

        String updateString =
        "update players set online = ? where id = ?";

        try {connection=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWD);
                PreparedStatement updatePlayer = connection.prepareStatement(updateString);
         
        updatePlayer.setInt(1, online);
        updatePlayer.setInt(2, id);
        updatePlayer.executeUpdate();
        System.out.println("updated online");

        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }finally{
        try{    
            if(connection != null) connection.close();
        }catch(SQLException ex){}

      }
     return 1;
   }
   
   

   
   public static int createGame(int playerID){
       Connection connection = null;
        Statement statement = null;
       
        int newId = getGamesNum()+1;
        
        deck cardDeck = new deck();

        try{
            connection=DriverManager.getConnection
               (DB_URL,DB_USER,DB_PASSWD);


            Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //First create gameState
            ResultSet uprs = ps.executeQuery("SELECT * FROM gameState");

            uprs.moveToInsertRow();
            uprs.updateInt("id", newId);
            uprs.updateInt("winner", -1);
            uprs.updateString("cards1", "");
            uprs.updateString("cards2", "");
             uprs.updateString("cardsDeck", cardDeck.getCards());

            uprs.insertRow();
            uprs.beforeFirst();

            
            
            //Then create the game, refrencing the gameState
            uprs = ps.executeQuery("SELECT * FROM games");

            uprs.moveToInsertRow();
            uprs.updateInt("active", 0);
            uprs.updateInt("id", newId);
            uprs.updateInt("player1", playerID);
            uprs.updateInt("winner", -1);
            uprs.insertRow();
            uprs.beforeFirst();


            System.out.println("Updated table games...");
            addGamesNum();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
          try{
            
              if(connection != null) connection.close();
          }catch(SQLException e){}

        }//end of finally



    return -1;
   }
   
   public static int deleteGame(int id){
       return -1;
   }
   
   public static int addPlayerGame(int playerID, int gameID){
       return -1;
   }
   
   public static int setGameActive(int id){
       return -1;
   }
   
   public static player getGamePlayers(int id, boolean first){
       return new player();
   }
   
   public static int getWinner(int id){
       return -1;
   }
   
   public static int setWinner(int gameID, int playerID){
       return -1;
   }

        
}
    

