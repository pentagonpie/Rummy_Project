package com.rummy.ui.gameEvents;

import com.rummy.shared.Game;
import com.rummy.shared.GameEndReason;

import java.util.EventListener;

public interface GameEndedEventListener extends EventListener {
    void onGameEnded(Game game, GameEndReason gameEndReason);
}
