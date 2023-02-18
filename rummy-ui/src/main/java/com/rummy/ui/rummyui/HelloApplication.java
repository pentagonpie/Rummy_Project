package com.rummy.ui.rummyui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, NotBoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        boolean result = new RMIClient().login("nadav", "123456");
        stage.setTitle(result ? "Hello Nadav!" : "Hello guest");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}