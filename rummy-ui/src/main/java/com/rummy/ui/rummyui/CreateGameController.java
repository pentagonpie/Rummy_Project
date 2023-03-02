package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    protected void onCreateNewGameClick() {
        final String userName = DataManager.getUserName();
        final Game createdGame = this.rmiClient.createGame(gameName.getText(), userName);
        FXMLLoader gameScreenLoader = new FXMLLoader(RummyApplication.class.getResource("gameScreen.fxml"));

        try {
            Scene createGameScene = new Scene(gameScreenLoader.load());
            Stage newStage = new Stage();
            newStage.setScene(createGameScene);
            GameController gameController = gameScreenLoader.getController();
            gameController.startGame(createdGame);

            newStage.show();

            Stage currentStage = (Stage) btnCreateGame.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at new game screen controller");
            alert.show();
        }
    }
}
