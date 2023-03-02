package com.rummy.shared;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Game implements Serializable {
    private final String name;
    private final String creator;

    public Game(String name, String creator) {
        super();
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return this.name;
    }

    public String getCreator() {
        return this.creator;
    }
}