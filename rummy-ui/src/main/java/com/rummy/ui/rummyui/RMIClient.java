package com.rummy.ui.rummyui;

import com.rummy.shared.*;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.ui.gameEvents.GameEventsManager;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class RMIClient implements Serializable, RummyClient {
    private final RummyServer server;
    private static RMIClient rmiClient;

    private RMIClient() throws RemoteException, NotBoundException {
        UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        server = (RummyServer) registry.lookup("rummyServer");
    }

    public static synchronized RMIClient getInstance() throws NotBoundException, RemoteException {
            if (rmiClient == null) {
                rmiClient = new RMIClient();
            }
            return rmiClient;
    }

    public String login(String username, String password) {
        try {
            return server.login(username, password, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int createUser(String name, String password) {
        try {
            System.out.println("hello inside RMIclientt, createUser " + name + ", " + password);
            
            return server.createUser(name, password);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
            
        }
    }

    public Game createGame(String name, String creatorUserName) {
        try {
            System.out.println("hello inside RMIclientt," + name + ", " + creatorUserName);
            
            return server.createNewGame(name, creatorUserName);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
            
        }
    }

    public String getPlayerName(String id){
        try {
            return server.getPlayerName(id);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        
    }

    public void deleteGame(Game game){
        try {
            server.deleteGame(game);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    
    public ArrayList<Game> getGames() {
        try {
            return server.getGames();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleGameStart(Game game) throws RemoteException {
        GameEventsManager.emitGameStartEvent(game);
    }

    @Override
    public void handleGameEnd(Game game, GameEndReason gameEndReason) throws RemoteException {
        GameEventsManager.emitGameEndEvent(game, gameEndReason);
    }
    
    //@Override
    public void handleNextTurn(Game game) throws RemoteException {
        GameEventsManager.emitNextTurn(game);
    }

    @Override
    public void handleGameMove(Game game) throws RemoteException {
        GameEventsManager.emitGameMoveEvent(game);
    }

    public void exitGame(String gameName, String playerId){
       try {
            server.exitGame(gameName,playerId );
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    
    public void increaseScore(String playerId){
       try {
            server.increaseScore(playerId );
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
   
    
    public void joinGame(String id, String playerId) throws RemoteException {
        this.server.joinGame(id, playerId);
    }
    
    public void nextTurn(Game game){
        try {
            this.server.nextTurn(game);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public MoveValidationResult addGameMove(GameMove gameMove) throws RemoteException{
        
        return this.server.addGameMove(gameMove);

    }
}
