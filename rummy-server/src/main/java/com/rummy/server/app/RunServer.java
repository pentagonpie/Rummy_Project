package com.rummy.server.app;

import com.rummy.shared.RummyServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RunServer {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        RummyServer rummyServer = new ServerImpl();

        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("rummyServer", rummyServer);
        System.out.println("Server is running!");
    }
}
