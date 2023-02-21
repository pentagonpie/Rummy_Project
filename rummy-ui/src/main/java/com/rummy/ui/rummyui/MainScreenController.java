/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rummy.ui.rummyui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pentagonpie
 */
public class MainScreenController  {

    /**
     * Initializes the controller class.
     */
    private Button logout;
    
    @FXML
    protected void onLogoutClick()  {
        FXMLLoader fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("login.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Welcome to Rummy!");
            primaryStage.setScene(scene);
            primaryStage.show();
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
    

