package com.rummy.ui.rummyui;

import com.rummy.shared.Game;
import com.rummy.ui.gameEvents.GameEventsManager;
import com.rummy.ui.gameEvents.GameStartedEventListener;
import javafx.application.Platform;
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

import javafx.scene.input.KeyEvent;

public class CreateGameController implements GameStartedEventListener {
    private final RMIClient rmiClient;

    public CreateGameController() throws NotBoundException, RemoteException {
        this.rmiClient = RMIClient.getInstance();
        GameEventsManager.register(this);
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
        final String playerId = DataManager.getPlayerId();
        final Game createdGame = this.rmiClient.createGame(gameName.getText(), playerId);

        if (createdGame.getId().equals("-1")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Can't create game");
            alert.show();

            return;
        }
        String waitingLabel = "Game Name: " + createdGame.getName() + "\n\nWaiting for another player to join...";

        this.lblWaitingText.setText(waitingLabel);
        this.createNewGameContainer.setVisible(false);
        this.lblWaitingText.setVisible(true);
    }

    @FXML
    protected void checkIfEnterKey(KeyEvent e) {
        if (e.getCode().toString().equals("ENTER")) {
            onCreateNewGameClick();
        }
    }

    public void deleteGame(Game game) {

        this.rmiClient.exitGame(game.getName(), DataManager.getPlayerId());
        this.rmiClient.deleteGame(game);
    }

    @Override
    public void onGameStarted(Game game) {
        Platform.runLater(() -> {
            DataManager.setGame(game);
            FXMLLoader gameScreenLoader = new FXMLLoader(RummyApplication.class.getResource("gameScreen.fxml"));
            Stage gameScreenStage = new Stage();
            gameScreenStage.show();
            gameScreenStage.setMaximized(true);
            Stage primaryStage = (Stage) btnCreateGame.getScene().getWindow();
            try {
                gameScreenStage.setScene(new Scene(gameScreenLoader.load()));
                primaryStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("IOException");
                alert.setHeaderText("Exception at create game screen controller");
                alert.show();
            }

            gameScreenStage.setOnCloseRequest(we -> {
                String userId = DataManager.getPlayerId();
                this.rmiClient.logout(userId );
                deleteGame(game);
                Platform.exit();
                System.exit(0);
            });
        });
    }
}
