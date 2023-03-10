package com.rummy.ui.gameEvents;

import com.rummy.shared.Game;

import java.util.EventListener;

public interface GameStartedEventListener extends EventListener {
    void onGameStarted(Game game);
}
