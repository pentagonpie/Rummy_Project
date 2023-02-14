package com.rummy.client.app;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class RunClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        RMIClient client = new RMIClient();

        Scanner in = new Scanner(System.in);

        System.out.print("enter username: ");
        String username = in.nextLine();
        System.out.print("enter password: ");
        String password = in.nextLine();

        boolean result = client.login(username, password);
        System.out.println("result is: " + result);
    }
}
