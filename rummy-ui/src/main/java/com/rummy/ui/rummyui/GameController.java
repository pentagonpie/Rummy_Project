package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController {
    private Game game;

    @FXML
    protected Label gameName;

    void startGame(Game game) {
        this.game = game;
        this.gameName.setText(this.game.getName());
    }
}
