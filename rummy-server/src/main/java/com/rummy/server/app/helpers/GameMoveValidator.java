package com.rummy.server.app.helpers;

import com.rummy.shared.MoveValidationResult;
import com.rummy.shared.Card;
import com.rummy.shared.Game;
import com.rummy.shared.GameState;
import com.rummy.shared.Suit;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;

import java.util.ArrayList;
import java.util.Comparator;

public class GameMoveValidator {
    private static  MoveValidationResult isValidDraw(Game game, GameMove gameMove) {
        System.out.println("checking if valid draw");
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByOpponent = !lastGameMove.getPlayerId().equals(gameMove.getPlayerId());
        //boolean lastMoveWasDiscard = lastGameMove.getGameMoveEventType() == GameMoveEventType.DISCARD;
        GameState state = game.getGameState();
        boolean discardPileEmpty = state.getDiscardPile().isEmpty();
        
        if(lastMoveDoneByOpponent && !discardPileEmpty){
            return new MoveValidationResult(true,0);
        }else{
            return new MoveValidationResult(false,2);
        }
    }

    private static MoveValidationResult isValidDiscard(Game game, GameMove gameMove) {
        System.out.println("checking if valid discard");
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByCurrPlayer = lastGameMove.getPlayerId().equals(gameMove.getPlayerId());
        boolean lastMoveWasDiscard = lastGameMove.getGameMoveEventType() == GameMoveEventType.DISCARD;
        if(lastMoveWasDiscard){
            return new MoveValidationResult(false,3);
        }
        if (lastMoveDoneByCurrPlayer && !lastMoveWasDiscard) {
            return new MoveValidationResult(true,0);
        }

         return new MoveValidationResult(false,6);
    }

    private static MoveValidationResult areCardsSameValue(ArrayList<Card> cards) {
        Card firstCard = cards.get(0);
        int firstCardValue = firstCard.getValue();

        for (int i = 1; i < cards.size(); i++) {
            Card currCard = cards.get(i);

            if (currCard.getValue() != firstCardValue) {
                return new MoveValidationResult(false,7);
            }
        }

        return new MoveValidationResult(true,0);
    }

    private static MoveValidationResult areCardsSameSuit(ArrayList<Card> cards) {
        Card firstCard = cards.get(0);
        Suit firstCardSuit = firstCard.getSuit();

        for (int i = 1; i < cards.size(); i++) {
            Card currCard = cards.get(i);

            if (currCard.getSuit() != firstCardSuit) {
                return new MoveValidationResult(false,8);
            }
        }

        return new MoveValidationResult(true,0);
    }

    private static void sortCards(ArrayList<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }

    private static MoveValidationResult isValidSeries(ArrayList<Card> cards) {
        if (areCardsSameValue(cards).isValid()) {
            return new MoveValidationResult(true,0);
        }

        if (!areCardsSameSuit(cards).isValid()) {
            return new MoveValidationResult(false,8);
        }

        sortCards(cards);

        for (int i = 0; i < cards.size() - 1; i++) {
            Card currCard = cards.get(i);
            Card nextCard = cards.get(i + 1);

            if (currCard.getValue() != nextCard.getValue() - 1) {
                return new MoveValidationResult(false,9);
            }
        }

        return new MoveValidationResult(true,0);
    }

    private static MoveValidationResult isValidMeld(Game game, GameMove gameMove) {
        System.out.println("checking meld");
        GameMove lastGameMove = game.getGameState().getLastMove();
        boolean lastMoveDoneByCurrPlayer = lastGameMove.getPlayerId().equals(gameMove.getPlayerId());
        boolean lastMoveWasDiscard = lastGameMove.getGameMoveEventType() == GameMoveEventType.DISCARD;

        if (!lastMoveDoneByCurrPlayer || lastMoveWasDiscard) {
            return new MoveValidationResult(false,4);
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

            return new MoveValidationResult(false,4);
        }
        if( !isValidSeries(gameMove.getCardsToMove()).isValid() ){
            return new MoveValidationResult(false,8);
        }
        
        
        if( gameMove.getCardsToMove().size() >= 3 ){
            return new MoveValidationResult(true,0);
        }else{
            return new MoveValidationResult(false,4);
        }
    }

    public static MoveValidationResult isValidMove(Game game, GameMove gameMove) {
        System.out.println("checking if valid move");
        if (gameMove == null || game == null || !game.getPlayersIds().contains(gameMove.getPlayerId())) {
            return new MoveValidationResult(false,1);
            
        }

        GameMoveEventType gameMoveEventType = gameMove.getGameMoveEventType();

        if (gameMoveEventType == null) {
            return new MoveValidationResult(false,1);
           
        }

        boolean isGameCreator = game.getCreator().equals(gameMove.getPlayerId());
        boolean isPlayerTurn = game.getGameState().getTurn() == 0 && isGameCreator || game.getGameState().getTurn() == 1 && !isGameCreator;

        if (!isPlayerTurn) {
            return new MoveValidationResult(false,5);
            
        }

        boolean isFirstGameMove = game.getGameState().getLastMove() == null;
        boolean isDrawMove = gameMoveEventType == GameMoveEventType.DRAW_FROM_DECK || gameMoveEventType == GameMoveEventType.DRAW_FROM_DISCARD;

        if (isFirstGameMove && isDrawMove) {
            return new MoveValidationResult(true,0);
            
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

        return new MoveValidationResult(false,1);
    }

}
