package com.rummy.server.app;

import com.rummy.shared.Game;
import com.rummy.shared.RummyServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ServerImpl implements RummyServer {
    private final Map<String, Game> games = new HashMap<>();

    public ServerImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        Map<String, String> usersMap = Map.of(
                "nadav", "123456",
                "tom", "123456"
        );

        return usersMap.containsKey(username) && usersMap.get(username).equals(password);
    }

    @Override
    public Game createNewGame(String gameName, String creatorUserName) throws RemoteException {
        Game createdGame = new Game(gameName, creatorUserName);
        this.games.put(gameName, createdGame);

        return createdGame;
    }
}
