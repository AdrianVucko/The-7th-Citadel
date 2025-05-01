package com.example.the7thcitadel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TheSeventhCitadelApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TheSeventhCitadelApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/main-view.css")).toExternalForm());
        stage.setTitle("The 7th Citadel");
        stage.setScene(scene);
        stage.show();
    }

    public static void main() {
        launch();
    }
}