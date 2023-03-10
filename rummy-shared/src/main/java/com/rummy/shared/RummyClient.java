package com.rummy.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RummyClient extends Remote {
    void handleGameStart(Game game) throws RemoteException;
}
