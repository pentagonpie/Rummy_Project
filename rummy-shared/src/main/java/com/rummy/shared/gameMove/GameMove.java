package com.rummy.shared.gameMove;

import com.rummy.shared.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMove implements Serializable {
    private String _playerId;
    private GameMoveEventType gameMoveEventType;
    private ArrayList<Card> _cardsToMove;
    private Card _destinationCard;
    private String _gameId;

    public GameMove(String playerId, GameMoveEventType gameMoveEventType, ArrayList<Card> cardsToMove, Card destinationCard, String gameId) {
        _playerId = playerId;
        this.gameMoveEventType = gameMoveEventType;
        _cardsToMove = cardsToMove;
        _destinationCard = destinationCard;
        _gameId = gameId;
    }

    public String getPlayerId() {
        return _playerId;
    }

    public GameMoveEventType getGameMoveEventType() {
        return gameMoveEventType;
    }

    public ArrayList<Card> getCardsToMove() {
        return _cardsToMove;
    }

    public Card getDestinationCard() {
        return _destinationCard;
    }

    public String getGameId() {
        return _gameId;
    }
}
