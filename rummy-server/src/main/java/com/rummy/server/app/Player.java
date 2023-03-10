package com.rummy.server.app;

import com.rummy.shared.RummyClient;

public class Player {
    private String _userId;
    private String _userName;
    private RummyClient _client;

    public Player(String userId, String userName, RummyClient client) {
        this._userId = userId;
        this._userName = userName;
        this._client = client;
    }

    public String getUserId() {
        return this._userId;
    }

    public String getUserName() {
        return this._userName;
    }

    public RummyClient getClient() {
        return this._client;
    }
}
