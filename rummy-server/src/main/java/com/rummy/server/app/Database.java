package com.rummy.server.app;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;

class player{
    
    
}
public class Database {
    //Saving database url and login credentials
    //"jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=CONVERT_TO_NULL";
   static final String DB_URL =
      "jdbc:mysql://localhost:3306/rummydb?zeroDateTimeBehavior=CONVERT_TO_NULL";
   static final String DB_USER = "root";
   static final String DB_PASSWD = "#!#bookclub#!#";

   
   
   public static void main(String[] args){
      
      Scanner myObj = new Scanner(System.in);  // Create a Scanner object
      
      //Endless loop to show user all command options
      while(true){
          System.out.println("show,insert,update,raise, or exit:");

      String choice = myObj.nextLine();  // Read user input
      
      if(choice.equals("show")){
        //showTable();
      }

      }//end of while true
     
   }//end of main
   
   public static int createPlayer(String name){
   
    return -1;
   }
   
   public static int getPlayerScore(int id){
       return -1;
   } 
   
    public static int setPlayerScore(int id, int score){
     return -1;
   } 
   
   public static int getPlayerOnline(int id){
       return -1;
   }
   
   public static int setPlayerOnline(int id, int online){
       return -1;
   }
   
   public static int createGame(int playerID){
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
    

