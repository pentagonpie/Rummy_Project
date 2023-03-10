package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController {
    private Game game;

    @FXML
    protected Label gameName;

    @FXML
    void initialize() {
        this.game = DataManager.getGame();
        this.gameName.setText("creator: " + game.getCreator() + ", game name: " + this.game.getName());
    }
}
