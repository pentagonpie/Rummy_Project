package com.rummy.shared;

import java.io.Serializable;

public class Card implements Serializable {
    private final int value;
    private final Suit suit;

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toString() {
        String result = "";
        result += this.getValue();
        result += this.getSuit().name();
        return result;
    }
}