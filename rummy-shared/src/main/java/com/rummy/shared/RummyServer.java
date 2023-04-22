package com.rummy.shared;

import com.rummy.shared.gameMove.GameMove;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RummyServer extends Remote {
    String login(String username, String password, RummyClient client) throws RemoteException;

    void logout(String userId) throws RemoteException;

    Game createNewGame(String gameName, String playerId) throws RemoteException;

    void joinGame(String gameName, String playerId) throws RemoteException;

    String getPlayerName(String id) throws RemoteException;

    void exitGame(String gameName, String playerId) throws RemoteException;

    void deleteGame(String gameID) throws RemoteException;
    
    boolean checkGameActive(int id) throws RemoteException;
    
    public int getScore(String userId) throws RemoteException;

    ArrayList<Game> getGames() throws RemoteException;

    MoveValidationResult addGameMove(GameMove gameMove) throws RemoteException;

    int createUser(String name, String password) throws RemoteException;
}
