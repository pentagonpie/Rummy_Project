package com.rummy.server.app;

public class User {
    String _id;
    String _userName;
    String _password;

    public User(String id, String userName, String password) {
        this._id = id;
        this._userName = userName;
        this._password = password;
    }

    public String getId() {
        return _id;
    }

    public String getUserName() {
        return _userName;
    }

    public String getPassword() {
        return _password;
    }
}
