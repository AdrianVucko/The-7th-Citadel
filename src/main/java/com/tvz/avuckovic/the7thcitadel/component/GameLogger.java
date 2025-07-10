package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.model.Message;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class GameLogger {

    private static TextArea logArea;

    public static void attach(TextArea area) {
        logArea = area;
        log(Message.LOG_INITIALIZED.getText());
    }

    public static void info(String message) {
        log("[INFO]" + getPlayerNameOrEmpty() + " " + message);
    }

    public static void warn(String message) {
        log("[WARN]" + getPlayerNameOrEmpty() + " " + message);
    }

    public static void error(String message) {log("[ERROR]" + getPlayerNameOrEmpty() + " " + message);}

    public static void log(String message) {
        if (logArea != null) {
            Platform.runLater(() -> logArea.appendText(message + "\n"));
        } else {
            System.out.println(message);
        }
    }

    private static String getPlayerNameOrEmpty() {
        Player player = Player.getInstance();
        if(player == null || player.getName() == null || player.getName().isBlank()) {
            return "";
        }
        return "[" + player.getName() + "]";
    }
}
