package com.tvz.avuckovic.the7thcitadel;

import com.tvz.avuckovic.the7thcitadel.model.ActiveScene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public StackPane sceneContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayGame();
    }

    public void displayGame() {
        displayScene(ActiveScene.MAIN);
    }

    public void displayRules() {
        displayScene(ActiveScene.RULES);
    }

    public void displayAbout() {
        displayScene(ActiveScene.ABOUT);
    }

    private void displayScene(ActiveScene activeScene) {
        try {
            saveMenuItem.setDisable(activeScene != ActiveScene.MAIN);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(activeScene.getFilePath()));
            Parent content = loader.load();
            sceneContainer.getChildren().setAll(content);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
