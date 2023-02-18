package com.rummy.ui.rummyui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LoginController {
    private final RMIClient rmiClient;

    public LoginController() throws NotBoundException, RemoteException {
        this.rmiClient = new RMIClient();
    }

    @FXML
    protected TextField username;

    @FXML
    protected TextField password;

    @FXML
    protected void onLoginClick() {
        final String username = this.username.getText();
        final String password = this.password.getText();
        final Boolean loginResult = this.rmiClient.login(username, password);

        if (loginResult) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login succeeded");
            alert.setHeaderText("Welcome " + username);
            alert.show();
        }
    }
}