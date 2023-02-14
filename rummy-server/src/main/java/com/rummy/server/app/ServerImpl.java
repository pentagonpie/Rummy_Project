package com.rummy.server.app;

import com.rummy.shared.RummyServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class ServerImpl implements RummyServer {
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
}
