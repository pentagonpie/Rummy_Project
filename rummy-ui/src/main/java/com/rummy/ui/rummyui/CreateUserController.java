package com.rummy.ui.rummyui;


import java.io.IOException;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class CreateUserController {
    private final RMIClient rmiClient;

    public CreateUserController() throws NotBoundException, RemoteException {
        this.rmiClient = RMIClient.getInstance();
        
    }

    @FXML
    private Button btnCreateUser;
    
    @FXML
    private Button btnBack;

    @FXML
    protected TextField usernameInput;
    
    @FXML
    protected TextField passwordInput;


    @FXML
    protected void onCreateNewUserClick() {
        String password = passwordInput.getText();
        String username = usernameInput.getText();
        int result = this.rmiClient.createUser(username, password);
        if(result ==-1){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Can't create user");
            alert.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("User created");
            alert.show();
        }


    }
    
    @FXML
    protected void onBackClick() {
         FXMLLoader mainScreenLoader = new FXMLLoader(RummyApplication.class.getResource("login.fxml"));
        Stage mainScreenStage = new Stage();
        mainScreenStage.show();
        mainScreenStage.setMaximized(true);

        mainScreenStage.setOnCloseRequest(we -> {
            Platform.exit();
            System.exit(0);
        });

        try {
            mainScreenStage.setScene(new Scene(mainScreenLoader.load()));
            Stage primaryStage = (Stage) btnBack.getScene().getWindow();
            primaryStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at game screen controller");
            alert.show();
        }
    }
    
    
}
