/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rummy.ui.rummyui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pentagonpie
 */
public class MainScreenController {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button btnLogout;

    @FXML
    private Button btnNewGame;

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
            Stage primaryStage = (Stage) btnNewGame.getScene().getWindow();
            primaryStage.close();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at main screen controller");
            alert.show();
        }
    }

    @FXML
    protected void onLogoutClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("login.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage newStage = new Stage();
            newStage.setTitle("Welcome to Rummy!");
            newStage.setScene(scene);
            newStage.show();

            //Close login window
            Stage primaryStage = (Stage) btnLogout.getScene().getWindow();
            primaryStage.close();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at main screen controller");
            alert.show();
        }
    }
}

//  @Override
// public void initialize(URL url, ResourceBundle rb) {
// TODO
//}
    
