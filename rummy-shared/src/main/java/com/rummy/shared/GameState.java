package com.rummy.shared;

import com.rummy.shared.gameMove.GameMove;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private ArrayList<Card> _cards1;
    private ArrayList<Card> _cards2;
    private ArrayList<Card> _deck;
    private ArrayList<ArrayList<Card>> _board;
    private int _turn;
    private ArrayList<Card> _discardPile;
    private GameMove _lastMove;

    public GameState(ArrayList<Card> cards1, ArrayList<Card> cards2, ArrayList<Card> deck, ArrayList<Card> discardPile, ArrayList<ArrayList<Card>> board) {
        super();
        this._cards1 = cards1;
        this._cards2 = cards2;
        this._deck = deck;
        this._board = board;
        this._discardPile = discardPile;
        this._lastMove = null;
    }

    public ArrayList<Card> getCards1() {
        return this._cards1;
    }

    public ArrayList<Card> getCards2() {
        return this._cards2;
    }

    public ArrayList<Card> getDeck() {
        return this._deck;
    }

    public void setCards1(ArrayList<Card> cards1) {
        this._cards1 = cards1;
    }

    public void setCards2(ArrayList<Card> cards2) {
        this._cards2 = cards2;
    }

    public void setDeck(ArrayList<Card> deck) {
        this._deck = deck;
    }

    public ArrayList<ArrayList<Card>> getBoard() {
        return this._board;
    }

    public void setBoard(ArrayList<ArrayList<Card>> board) {
        this._board = board;
    }

    public int getTurn() {
        return this._turn;
    }

    public void nextTurn() {
        if (this._turn == 0) {
            this._turn = 1;
        } else {
            this._turn = 0;
        }
    }

    public ArrayList<Card> getDiscardPile() {
        return this._discardPile;
    }

    public void setDiscardPile(ArrayList<Card> discardPile) {
        this._discardPile = discardPile;
    }

    public GameMove getLastMove() {
        return this._lastMove;
    }

    public void setLastMove(GameMove lastMove) {
        this._lastMove = lastMove;
    }

}