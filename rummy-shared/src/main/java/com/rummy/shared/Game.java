package com.rummy.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private final String _id;
    private final String _name;
    private final String _creator;
    private List<String> _playersIds;
    private String _secondPlayer;

    private final GameState _gameState;

    public Game(String name, String creator, GameState gameState, String id) {
        super();
        this._id = id;
        this._name = name;
        this._creator = creator;
        this._playersIds = new ArrayList<>();
        this._gameState = gameState;
        this._secondPlayer = "";
    }

    //Used for returning null game for error indication
    public Game(String name, String creator, String id) {
        super();
        this._id = id;
        this._name = name;
        this._creator = creator;
        this._gameState = null;

    }

    public String getName() {
        return this._name;
    }

    public String getCreator() {
        return this._creator;
    }

    public List<String> getPlayersIds() {
        return this._playersIds;
    }

    public void addPlayer(String playerId) {
        this._secondPlayer = playerId;
        this._playersIds.add(playerId);
    }

    public String getSecondPlayer() {
        return this._secondPlayer;
    }

    public String getId() {
        return this._id;
    }

    public GameState getGameState() {
        return this._gameState;
    }

    public void nextTurn() {
        this._gameState.nextTurn();
    }
}