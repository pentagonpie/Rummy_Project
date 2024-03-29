package com.rummy.ui.gameEvents;

import com.rummy.shared.Game;
import com.rummy.shared.GameEndReason;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class GameEventsManager {
    private static List<EventListener> listeners= new ArrayList();

    public static void register(EventListener listener){
        listeners.add(listener);
    }
    public static void emitGameStartEvent(Game game){
        for(EventListener listener: listeners){
            if (listener instanceof GameStartedEventListener) {
                ((GameStartedEventListener) listener).onGameStarted(game);
            }
        }
    }

    public static void emitGameEndEvent(Game game, GameEndReason gameEndReason){
    for(EventListener listener: listeners){
        if (listener instanceof GameEndedEventListener) {
            ((GameEndedEventListener) listener).onGameEnded(game, gameEndReason);
        }
    }
    }

    public static void emitNextTurn(Game game){
    for(EventListener listener: listeners){
        if (listener instanceof nextTurnEventListener) {
            ((nextTurnEventListener) listener).onNextTurn(game);
        }
    }
    }

    public static void emitGameMoveEvent(Game game) {
        for(EventListener listener: listeners){
            if (listener instanceof GameMoveEventListener) {
                ((GameMoveEventListener) listener).onGameMove(game);
            }
        }
    }
}
