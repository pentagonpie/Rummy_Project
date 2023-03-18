package com.rummy.server.app.helpers;

import com.rummy.shared.Card;
import com.rummy.shared.Game;
import com.rummy.shared.Suit;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;

import java.util.ArrayList;
import java.util.Comparator;

public class GameMoveValidator {
    private static boolean isValidDraw(Game game, GameMove gameMove) {
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByOpponent = lastGameMove.getPlayerId() != gameMove.getPlayerId();
        boolean lastMoveWasDiscard = lastGameMove.getGameMoveEventType() == GameMoveEventType.DISCARD;

        if (lastMoveDoneByOpponent && lastMoveWasDiscard) {
            return true;
        }

        return false;
    }

    private static boolean isValidDiscard(Game game, GameMove gameMove) {
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByCurrPlayer = lastGameMove.getPlayerId() == gameMove.getPlayerId();
        boolean lastMoveWasDiscard = lastGameMove.getGameMoveEventType() == GameMoveEventType.DISCARD;

        if (lastMoveDoneByCurrPlayer && !lastMoveWasDiscard) {
            return true;
        }

        return false;
    }

    private static boolean areCardsSameValue(ArrayList<Card> cards) {
        Card firstCard = cards.get(0);
        int firstCardValue = firstCard.getValue();

        for (int i = 1; i < cards.size(); i++) {
            Card currCard = cards.get(i);

            if (currCard.getValue() != firstCardValue) {
                return false;
            }
        }

        return true;
    }

    private static boolean areCardsSameSuit(ArrayList<Card> cards) {
        Card firstCard = cards.get(0);
        Suit firstCardSuit = firstCard.getSuit();

        for (int i = 1; i < cards.size(); i++) {
            Card currCard = cards.get(i);

            if (currCard.getSuit() != firstCardSuit) {
                return false;
            }
        }

        return true;
    }

    private static void sortCards(ArrayList<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }

    private static boolean isValidSeries(ArrayList<Card> cards) {
        if (areCardsSameValue(cards)) {
            return true;
        }

        if (!areCardsSameSuit(cards)) {
            return false;
        }

        sortCards(cards);

        for (int i = 0; i < cards.size() - 1; i++) {
            Card currCard = cards.get(i);
            Card nextCard = cards.get(i + 1);

            if (currCard.getValue() != nextCard.getValue() - 1) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidMeld(Game game, GameMove gameMove) {
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByCurrPlayer = lastGameMove.getPlayerId() == gameMove.getPlayerId();
        boolean lastMoveWasDraw = lastGameMove.getGameMoveEventType() == GameMoveEventType.DRAW_FROM_DECK;

        if (!lastMoveDoneByCurrPlayer || !lastMoveWasDraw) {
            return false;
        }

        Card destinationCard = gameMove.getDestinationCard();
        boolean isAddToExistingSeries = destinationCard != null;

        if (isAddToExistingSeries) {
            ArrayList<ArrayList<Card>> board = game.getGameState().getBoard();

            for (ArrayList<Card> meld : board) {
                if (meld.contains(gameMove.getDestinationCard())) {
                    ArrayList<Card> cardsToCheckAsSeries = new ArrayList<>();
                    cardsToCheckAsSeries.addAll(gameMove.getCardsToMove());
                    cardsToCheckAsSeries.add(gameMove.getDestinationCard());
                    return isValidSeries(new ArrayList(cardsToCheckAsSeries));
                }
            }

            return false;
        }

        return gameMove.getCardsToMove().size() >= 3 && isValidSeries(gameMove.getCardsToMove());
    }

    public static boolean isValidMove(Game game, GameMove gameMove) {
        if (gameMove == null || game == null || !game.getPlayersIds().contains(gameMove.getPlayerId())) {
            return false;
        }

        GameMoveEventType gameMoveEventType = gameMove.getGameMoveEventType();

        if (gameMoveEventType == null) {
            return false;
        }

        boolean isGameCreator = game.getCreator().equals(gameMove.getPlayerId());
        boolean isPlayerTurn = game.getGameState().getTurn() == 0 && isGameCreator || game.getGameState().getTurn() == 1 && !isGameCreator;

        if (!isPlayerTurn) {
            return false;
        }

        boolean isFirstGameMove = game.getGameState().getLastMove() == null;
        boolean isDrawMove = gameMoveEventType == GameMoveEventType.DRAW_FROM_DECK || gameMoveEventType == GameMoveEventType.DRAW_FROM_DISCARD;

        if (isFirstGameMove && isDrawMove) {
            return true;
        }

        if (isDrawMove) {
            return isValidDraw(game, gameMove);
        }

        if (gameMoveEventType == GameMoveEventType.DISCARD) {
            return isValidDiscard(game, gameMove);
        }

        if (gameMoveEventType == GameMoveEventType.MELD) {
            return isValidMeld(game, gameMove);
        }

        return false;
    }

}
