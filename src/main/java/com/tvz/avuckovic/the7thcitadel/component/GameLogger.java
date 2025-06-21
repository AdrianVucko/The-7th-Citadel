package com.tvz.avuckovic.the7thcitadel.component;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class GameLogger {

    private static TextArea logArea;

    public static void attach(TextArea area) {
        logArea = area;
        log("📝 Game log initialized.");
    }

    public static void info(String message) {
        log("[INFO] " + message);
    }

    public static void warn(String message) {
        log("[WARN] " + message);
    }

    public static void error(String message) {
        log("[ERROR] " + message);
    }

    public static void log(String message) {
        if (logArea != null) {
            Platform.runLater(() -> logArea.appendText(message + "\n"));
        } else {
            System.out.println(message);
        }
    }
}
