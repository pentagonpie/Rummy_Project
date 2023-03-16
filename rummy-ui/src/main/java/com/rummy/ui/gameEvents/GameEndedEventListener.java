package com.rummy.ui.gameEvents;

import com.rummy.shared.Game;

import java.util.EventListener;

public interface GameEndedEventListener extends EventListener {
    void onGameEnded(Game game);
}
