package com.tvz.avuckovic.the7thcitadel;

import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.model.ApplicationConfiguration;
import com.tvz.avuckovic.the7thcitadel.model.Message;
import com.tvz.avuckovic.the7thcitadel.model.PlayerType;
import com.tvz.avuckovic.the7thcitadel.utils.DialogUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TheSeventhCitadelApplication extends Application {
    public static final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TheSeventhCitadelApplication.class.getResource(
                GameConstants.Page.ROOT_FOLDER + GameConstants.Page.ROOT));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(
                GameConstants.UI.STYLE_ROOT_FOLDER + GameConstants.UI.THEME)).toExternalForm());
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setTitle(GameConstants.UI.TITLE);
        stage.setScene(scene);
        // Full screen
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();
    }

    public static void main(String[] args) {
        configureApplicationExceptionHandler();
        if(args.length > 0) {
            applicationConfiguration.setPlayerType(PlayerType.valueOf(args[0]));
            launch();
        }
        else {
            Platform.runLater(() -> DialogUtils.showDialog(
                    Alert.AlertType.ERROR, Message.START_APPLICATION_ERROR_MESSAGE.getText(),
                    Message.START_APPLICATION_ERROR_MESSAGE.getText(), ""));
        }
    }

    private static void configureApplicationExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Throwable cause = unwrapCause(throwable);
            if (cause instanceof ApplicationException applicationException) {
                String message = applicationException.getMessage() != null ?
                        applicationException.getMessage() : Message.UNKNOWN_ERROR.getText();
                log(applicationException.getAlertType(), message);
                DialogUtils.showDialog(
                        applicationException.getAlertType(),
                        Message.SOMETHING_HAPPENED.getText(),
                        message,
                        ""
                );
            } else if (cause instanceof ConfigurationException configurationException) {
                String message = configurationException.getMessage() != null ?
                        configurationException.getMessage() : Message.UNKNOWN_ERROR.getText();
                log(Alert.AlertType.ERROR, message);
                DialogUtils.showDialog(
                        Alert.AlertType.ERROR,
                        Message.SOMETHING_HAPPENED.getText(),
                        Message.SUPPORT.getText(),
                        message
                );
            } else {
                throwable.printStackTrace();
            }
        });
    }

    private static Throwable unwrapCause(Throwable throwable) {
        Throwable result = throwable;
        while (result.getCause() != null && result != result.getCause()) {
            result = result.getCause();
        }
        return result;
    }

    private static void log(Alert.AlertType alertType, String message) {
        if(alertType.equals(Alert.AlertType.INFORMATION)) {
            GameLogger.info(message);
        } else if (alertType.equals(Alert.AlertType.WARNING)) {
            GameLogger.warn(message);
        } else {
            GameLogger.error(message);
        }
    }
}