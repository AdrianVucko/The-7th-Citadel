package com.tvz.avuckovic.the7thcitadel;

import com.tvz.avuckovic.the7thcitadel.controller.MainController;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.thread.PlayerOneServerThread;
import com.tvz.avuckovic.the7thcitadel.thread.PlayerTwoServerThread;
import com.tvz.avuckovic.the7thcitadel.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem replayMenuItem;
    @FXML
    public StackPane sceneContainer;

    private Object currentController;
    private GameState currentState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPlayerThreads();
        newGame();
    }

    public void displayRules() {
        displayScene(ActiveScene.RULES, GameMode.NONE);
    }

    public void displayAbout() {
        displayScene(ActiveScene.ABOUT, GameMode.NONE);
    }

    public void generateDocumentation() {
        DocumentationUtils.generateDocumentation();
    }

    public void newGame() {
        if (!PlayerType.PLAYER_TWO.name().equals(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name())) {
            GameMoveUtils.clearMoves();
        }
        clearLogger();
        displayScene(ActiveScene.MAIN, GameMode.NONE);
    }

    public void saveGame() {
        getCurrentController(MainController.class).ifPresent(MainController::saveGame);
    }

    public void loadGame() {
        if(!FileUtils.fileExists("dat/savedGame.ser")) {
            throw new ApplicationException("Game needs to be saved first");
        }
        GameMoveUtils.clearMoves();
        currentState = GameStateUtils.loadGame();
        GamePlayThreadsUtils.sendMove(currentState, false);
        displayScene(ActiveScene.MAIN, GameMode.LOAD);
    }

    public void loadGameManually(GameState gameState) {
        currentState = gameState;
        displayScene(ActiveScene.MAIN, GameMode.LOAD);
    }

    public void replayGame() {
        currentState = getCurrentController(MainController.class)
                .map(MainController::replayedState)
                .orElseThrow(() -> new ConfigurationException("Replay is not possible from other pages"));
        displayScene(ActiveScene.MAIN, GameMode.REPLAY);
    }

    private void displayScene(ActiveScene activeScene, GameMode gameMode) {
        try {
            saveMenuItem.setDisable(activeScene != ActiveScene.MAIN);
            replayMenuItem.setDisable(activeScene != ActiveScene.MAIN || isMultiplayer());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(activeScene.getFilePath()));
            Parent content = loader.load();
            currentController = loader.getController();
            getCurrentController(MainController.class).ifPresent(main -> initializeMainScene(main, gameMode));
            sceneContainer.getChildren().setAll(content);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void initializeMainScene(MainController mainController, GameMode gameMode) {
        if(gameMode.equals(GameMode.LOAD)) {
            mainController.init(currentState, GameMode.LOAD);
        } else if (gameMode.equals(GameMode.REPLAY)) {
            if(currentState == null) {
                throw new ConfigurationException("Replayed state does not exist");
            }
            mainController.init(currentState, GameMode.REPLAY);
        } else {
            mainController.init();
        }
    }

    private void clearLogger() {
        if (isMultiplayer()) {
            SharedUtils.clearLogs();
        }
    }

    private void startPlayerThreads() {
        if (PlayerType.PLAYER_TWO.name().equals(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name()))
        {
            PlayerTwoServerThread playerTwoServerThread = new PlayerTwoServerThread(this);
            Thread thread = new Thread(playerTwoServerThread);
            thread.start();
        } else if (PlayerType.PLAYER_ONE.name().equals(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name()))
        {
            PlayerOneServerThread playerOneServerThread = new PlayerOneServerThread(this);
            Thread thread = new Thread(playerOneServerThread);
            thread.start();
        }
    }

    private static boolean isMultiplayer() {
        return !TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.SINGLE_PLAYER);
    }

    public <T> Optional<T> getCurrentController(Class<T> controllerType) {
        if (controllerType.isInstance(currentController)) {
            return Optional.of(controllerType.cast(currentController));
        }
        return Optional.empty();
    }
}
