package com.rummy.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RummyServer extends Remote {
    boolean login(String username, String password) throws RemoteException;
    Game createNewGame(String creatorUserName, String gameName) throws RemoteException;
}
