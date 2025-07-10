package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.component.GameActionDialog;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DialogUtils {

    public static void showDialog(Alert.AlertType alertType,
                                  String title,
                                  String headerText,
                                  String contentText)
    {
        Platform.runLater( () -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.showAndWait();
        });
    }

    public static void showActionDialog(GameAction action)
    {
        Platform.runLater( () -> {
            GameActionDialog gameActionDialog = new GameActionDialog(action);
            gameActionDialog.showAndWait();
        });
    }
}