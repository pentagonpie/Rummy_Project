package com.rummy.server.app.helpers;

import com.rummy.shared.Card;
import com.rummy.shared.Game;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;

import java.util.ArrayList;

public class GameMoveExecutor {
    private static ArrayList<Card> getPlayerCards(Game game, GameMove gameMove) {
        boolean isGameCreator = game.getCreator().equals(gameMove.getPlayerId());
        return isGameCreator ? game.getGameState().getCards1() : game.getGameState().getCards2();
    }

    private static void removeCardFromPlayer(Game game, GameMove gameMove, Card cardToRemove) {
        ArrayList<Card> playerCards = getPlayerCards(game, gameMove);
        Card playerCardToRemove = playerCards.stream().filter(card -> card.getSuit() == cardToRemove.getSuit() && card.getValue() == cardToRemove.getValue()).findFirst().get();
        playerCards.remove(playerCardToRemove);
    }

    private static void removeCardsFromPlayer(Game game, GameMove gameMove, ArrayList<Card> cardsToRemove) {
        for (Card cardToRemove : cardsToRemove) {
            removeCardFromPlayer(game, gameMove, cardToRemove);
        }
    }



    private static Game handleDrawCardFromDeck(Game game, GameMove gameMove) {
        ArrayList<Card> deck = game.getGameState().getDeck();
        Card drawCard = deck.get(0);
        deck.remove(0);

        ArrayList<Card> playerCards = getPlayerCards(game, gameMove);
        playerCards.add(drawCard);

        boolean isDeckEmpty = deck.size() == 0;

        if (isDeckEmpty) {
            ArrayList<Card> discardPile = game.getGameState().getDiscardPile();
            Card firstFromDiscard = discardPile.get(0);
            discardPile.remove(0);
            deck.addAll(discardPile);
            discardPile.clear();
            discardPile.add(firstFromDiscard);
        }

        return game;
    }

    private static Game handleDrawCardFromDiscard(Game game, GameMove gameMove) {
        ArrayList<Card> discardPile = game.getGameState().getDiscardPile();
        int lastDiscardPileIndex = discardPile.size() - 1;
        Card drawCard = discardPile.get(lastDiscardPileIndex);
        discardPile.remove(lastDiscardPileIndex);

        ArrayList<Card> playerCards = getPlayerCards(game, gameMove);
        playerCards.add(drawCard);

        return game;
    }

    private static Game handleDiscardCard(Game game, GameMove gameMove) {
        Card cardToDiscard = gameMove.getCardsToMove().get(0);

        removeCardFromPlayer(game, gameMove, cardToDiscard);

        ArrayList<Card> discardPile = game.getGameState().getDiscardPile();
        discardPile.add(cardToDiscard);

        return game;
    }

    private static Game handleMeld(Game game, GameMove gameMove) {
        ArrayList<Card> playerCards = getPlayerCards(game, gameMove);
        playerCards.removeAll(gameMove.getCardsToMove());

        Card destinationCard = gameMove.getDestinationCard();
        ArrayList<ArrayList<Card>> board = game.getGameState().getBoard();
        if (destinationCard != null) {
            for (ArrayList<Card> series : board) {
                if (series.contains(destinationCard)) {
                    series.addAll(gameMove.getCardsToMove());
                    break;
                }
            }
        } else {
            board.add(gameMove.getCardsToMove());
        }

        removeCardsFromPlayer(game, gameMove, gameMove.getCardsToMove());

        return game;
    }

    public static Game executeGameMove(Game game, GameMove gameMove) {
        if (gameMove.getGameMoveEventType() == GameMoveEventType.DRAW_FROM_DECK) {
            return handleDrawCardFromDeck(game, gameMove);
        } else if (gameMove.getGameMoveEventType() == GameMoveEventType.DRAW_FROM_DISCARD) {
            return handleDrawCardFromDiscard(game, gameMove);
        } else if (gameMove.getGameMoveEventType() == GameMoveEventType.DISCARD) {
            return handleDiscardCard(game, gameMove);
        } else if (gameMove.getGameMoveEventType() == GameMoveEventType.MELD) {
            return handleMeld(game, gameMove);
        }

        return game;
    }
}
