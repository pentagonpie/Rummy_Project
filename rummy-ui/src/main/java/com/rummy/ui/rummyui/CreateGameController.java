package com.rummy.ui.rummyui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateGameController {

    @FXML
    private Button btnCreateGame;

    @FXML
    protected TextField gameName;

    @FXML
    protected void onCreateNewGameClick() {
        FXMLLoader createGameScreenLoader = new FXMLLoader(RummyApplication.class.getResource("createGameScreen.fxml"));
        Scene createGameScene;
        try {
            createGameScene = new Scene(createGameScreenLoader.load());
            Stage newStage = new Stage();
            newStage.setTitle("Create new game");
            newStage.setScene(createGameScene);
            newStage.show();
            Stage primaryStage = (Stage) btnCreateGame.getScene().getWindow();
            primaryStage.close();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at main screen controller");
            alert.show();
        }
    }
}
