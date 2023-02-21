package com.rummy.ui.rummyui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginController {
    private final RMIClient rmiClient;

    public LoginController() throws NotBoundException, RemoteException {
        this.rmiClient = new RMIClient();
    }

    @FXML
    protected TextField username;

    @FXML
    protected TextField password;
    private Button btnLogin;

    @FXML
    protected void onLoginClick() {
        final String username = this.username.getText();
        final String password = this.password.getText();
        final Boolean loginResult = this.rmiClient.login(username, password);

        if (loginResult) {

        try {
            //Parent fxmlLoader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            FXMLLoader  fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("mainScreen.fxml"));
            Scene scene;
            System.out.println("testing1");
            //scene = new Scene(fxmlLoader);
            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.setTitle("Welcome to Rummy!");
            
            //primaryStage.initOwner(btnLogin.getScene().getWindow());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at login screen controller");
            alert.show();
        }
            
            

        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login Failed");
            alert.setHeaderText("try again");
            alert.show();
        }
    }
}