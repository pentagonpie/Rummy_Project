package com.rummy.ui.rummyui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;

public class RummyApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, NotBoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(RummyApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Welcome to Rummy!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}