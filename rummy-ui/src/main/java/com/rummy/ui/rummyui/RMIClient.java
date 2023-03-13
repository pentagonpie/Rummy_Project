package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import com.rummy.shared.RummyClient;
import com.rummy.shared.RummyServer;
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

    public Game createGame(String name, String creatorUserName) {
        try {
            System.out.println("hello inside RMIclientt," + name + ", " + creatorUserName);
            
            return server.createNewGame(name, creatorUserName);
        } catch (RemoteException e) {
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

    public void joinGame(String id, String playerId) throws RemoteException {
        this.server.joinGame(id, playerId);
    }
}
