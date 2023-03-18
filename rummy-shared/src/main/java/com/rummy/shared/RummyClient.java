package com.rummy.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RummyClient extends Remote {
    void handleGameStart(Game game) throws RemoteException;
    void handleGameEnd(Game game) throws RemoteException;
    void handleNextTurn(Game game) throws RemoteException;

}
