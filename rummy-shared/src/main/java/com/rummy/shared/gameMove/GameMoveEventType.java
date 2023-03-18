package com.rummy.shared.gameMove;

import java.io.Serializable;

public enum GameMoveEventType implements Serializable {
    DRAW_FROM_DECK,
    DRAW_FROM_DISCARD,
    DISCARD,
    MELD,
}
