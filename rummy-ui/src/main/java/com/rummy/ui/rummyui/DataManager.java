package com.rummy.ui.rummyui;

import com.rummy.shared.Game;

public class DataManager {
    static private String userName;
    static private String playerId;

    static private Game game;

    static void setUserName(String userName) {
        DataManager.userName = userName;
    }

    static String getUserName() {
        return DataManager.userName;
    }

    static void setPlayerId(String playerId) {
        DataManager.playerId = playerId;
    }

    static String getPlayerId() {
        return DataManager.playerId;
    }

    static void setGame(Game game) {
        DataManager.game = game;
    }

    static Game getGame() {
        return DataManager.game;
    }
}
