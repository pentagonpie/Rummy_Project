package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CreateGameController {
    private final RMIClient rmiClient;

    public CreateGameController() throws NotBoundException, RemoteException {
        this.rmiClient = new RMIClient();
    }

    @FXML
    private Button btnCreateGame;

    @FXML
    protected TextField gameName;

    @FXML
    protected VBox createNewGameContainer;

    @FXML
    protected Label lblWaitingText;

    @FXML
    protected void onCreateNewGameClick() {
        final String userName = DataManager.getUserName();
        final Game createdGame = this.rmiClient.createGame(gameName.getText(), userName);
        String waitingLabel = "Game Name: " + createdGame.getName() + "\n\nWaiting for another player to join...";

        this.lblWaitingText.setText(waitingLabel);
        this.createNewGameContainer.setVisible(false);
        this.lblWaitingText.setVisible(true);
    }
}
