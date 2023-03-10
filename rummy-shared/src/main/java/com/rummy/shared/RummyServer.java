package com.rummy.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RummyServer extends Remote {
    String login(String username, String password, RummyClient client) throws RemoteException;
    void logout(String userId) throws RemoteException;
    Game createNewGame(String gameName, String playerId) throws RemoteException;
    void joinGame(String gameName, String playerId) throws RemoteException;
    ArrayList<Game> getGames() throws RemoteException;
}
