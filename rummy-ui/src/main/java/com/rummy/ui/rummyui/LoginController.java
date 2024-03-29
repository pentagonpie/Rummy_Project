package com.rummy.ui.rummyui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController {
    private final RMIClient rmiClient;

    public LoginController() throws NotBoundException, RemoteException {
        this.rmiClient = RMIClient.getInstance();
    }

    @FXML
    protected TextField username;

    @FXML
    protected TextField password;

    @FXML
    private Button btnLogin;

    @FXML
    protected void onCreateUserClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("createUserScreen.fxml"));

        Stage newStage = new Stage();
        newStage.show();

        newStage.setOnCloseRequest(we -> {
            
            Platform.exit();
            System.exit(0);
        });

        try {
            //Open main window
            newStage.setScene(new Scene(fxmlLoader.load()));
            newStage.setTitle("Create user");

            //Close login window
            Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
            primaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at login screen controller");
            alert.show();
        }
    }
    
    @FXML
    protected void checkIfEnterKey(KeyEvent e) {
        if (e.getCode().toString().equals("ENTER")) {
            onLoginClick();
        }
    }

    @FXML
    protected void onLoginClick() {
        final String username = this.username.getText();
        final String password = this.password.getText();
        final String userId = this.rmiClient.login(username, password);

        boolean isLoggedIn = userId != null;
        



        if (isLoggedIn) {
            
            if(userId.equals("loggedInAlready")){                       
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Login Failed");
                alert.setHeaderText("User logged in already");
                alert.show();
                return;
            }

            DataManager.setUserName(username);
            DataManager.setPlayerId(userId);

            try {
                //Open main window
                FXMLLoader fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("mainScreen.fxml"));

                Stage newStage = new Stage();
                newStage.setScene(new Scene(fxmlLoader.load()));
                newStage.setTitle("Welcome to Rummy!");
                newStage.show();

                newStage.setOnCloseRequest(we -> {
                    this.rmiClient.logout(userId);
                    Platform.exit();
                    System.exit(0);
                });

                //Close login window
                Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
                primaryStage.close();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("IOException");
                alert.setHeaderText("Exception at login screen controller");
                alert.show();
            }
        } else {
            //Wrong credentials, show alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login Failed");
            alert.setHeaderText("try again");
            alert.show();
        }
    }
}