package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import com.rummy.shared.RummyServer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private final RummyServer server;

    public RMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        server = (RummyServer) registry.lookup("rummyServer");
    }

    public boolean login(String username, String password) {
        try {
            return server.login(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Game createGame(String name, String creatorUserName) {
        try {
            return server.createNewGame(name, creatorUserName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
