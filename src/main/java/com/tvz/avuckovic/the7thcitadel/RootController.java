package com.tvz.avuckovic.the7thcitadel;

import com.tvz.avuckovic.the7thcitadel.chat.SharedLogService;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationKey;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationReader;
import com.tvz.avuckovic.the7thcitadel.model.ActiveScene;
import com.tvz.avuckovic.the7thcitadel.model.PlayerType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
        clearLogger();
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

    private void clearLogger() {
        if (isMultiplayer()) {
            try {
                Registry registry = LocateRegistry.getRegistry(
                        ConfigurationReader.getStringValue(ConfigurationKey.HOSTNAME),
                        ConfigurationReader.getIntegerValue(ConfigurationKey.RMI_PORT));
                SharedLogService sharedLogService = (SharedLogService) registry.lookup(SharedLogService.REMOTE_OBJECT_NAME);
                sharedLogService.clearLogs();
                GameLogger.clearLogs();
            } catch (RemoteException | NotBoundException e) {
                throw new ConfigurationException("An error occurred while clearing logs!", e);
            }
        }
    }

    private static boolean isMultiplayer() {
        return !TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.SINGLE_PLAYER);
    }
}
