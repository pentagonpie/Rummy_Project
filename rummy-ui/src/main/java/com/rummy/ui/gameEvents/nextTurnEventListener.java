package com.rummy.ui.gameEvents;

import com.rummy.shared.Game;

import java.util.EventListener;

public interface nextTurnEventListener extends EventListener {
    void onNextTurn(Game game);
}
