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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.EventListener;
import java.util.List;

public class JoinGameController implements GameStartedEventListener {
    private final RMIClient rmiClient;

    public JoinGameController() throws NotBoundException, IOException {
        this.rmiClient = RMIClient.getInstance();
        GameEventsManager.register((EventListener) this);
    }

    @FXML
    public void initialize() {
        List<Game> games = this.rmiClient.getGames();
        for (Game game : games) {
            
            if(this.rmiClient.checkGameActive(Integer.parseInt(  game.getId()))){
                continue;
            }
            HBox gameHBox = new HBox();
            Label gameNameLabel = new Label(game.getName());

            Button joinGameButton = new Button("Join Game");
            joinGameButton.setOnAction(event -> {
                try {
                    this.rmiClient.joinGame(game.getName(), DataManager.getPlayerId());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                this.vboxGames.setVisible(false);
            });
            gameHBox.getChildren().addAll(gameNameLabel, joinGameButton);
            this.vboxGames.getChildren().add(gameHBox);
        }
    }

    @FXML
    private VBox vboxGames;

    public void deleteGame(Game game) {
        this.rmiClient.exitGame(game.getName(), DataManager.getPlayerId());
        this.rmiClient.deleteGame(game);
    }

    @Override
    public void onGameStarted(Game game) {
        System.out.println("from onGameStarted, game id is " + game.getId());
        Platform.runLater(() -> {
            DataManager.setGame(game);
            FXMLLoader gameScreenLoader = new FXMLLoader(RummyApplication.class.getResource("gameScreen.fxml"));
            Stage gameScreenStage = new Stage();
            gameScreenStage.show();
            gameScreenStage.setMaximized(true);

            //Close app after closing window
            gameScreenStage.setOnCloseRequest(we -> {
                String userId = DataManager.getPlayerId();
                this.rmiClient.logout(userId);
                deleteGame(game);
                Platform.exit();
                System.exit(0);
            });

            try {
                gameScreenStage.setScene(new Scene(gameScreenLoader.load()));
                Stage primaryStage = (Stage) vboxGames.getScene().getWindow();
                primaryStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("IOException");
                alert.setHeaderText("Exception at create game screen controller");
                alert.show();
            }
        });
    }
}
