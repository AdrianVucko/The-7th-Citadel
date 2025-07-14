package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.chat.SharedLogService;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import com.tvz.avuckovic.the7thcitadel.utils.SharedUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GameLogger {

    private static SharedLogService sharedLogService;
    private static TextArea logArea;
    private static Integer loggedMessagesCount;

    public static void attach(SharedLogService logService) {
        sharedLogService = logService;
        loggedMessagesCount = 0;
    }

    public static void attach(TextArea loggingArea) {
        logArea = loggingArea;
    }

    public static void info(String message) {
        log(buildPrefix("[INFO]") + " " + message);
    }

    public static void warn(String message) {
        log(buildPrefix("[WARN]") + " " + message);
    }

    public static void error(String message) {
        log(buildPrefix("[ERROR]") + " " + message);
    }

    public static void log(String message) {
        if (sharedLogService != null) {
            SharedUtils.saveLogMessage(message, sharedLogService);
        } else if (logArea != null) {
            Platform.runLater(() -> logArea.appendText(message + "\n"));
        } else {
            log.info(message);
        }
    }

    public static List<String> filterLogs(List<String> allLogs) {
        return allLogs.subList(loggedMessagesCount, allLogs.size());
    }

    public static void storeNewLogs(List<String> logs) {
        loggedMessagesCount += logs.size();
    }

    public static void clearLogs() {
        loggedMessagesCount = 0;
    }

    private static String buildPrefix(String logType) {
       return logType + getPlayerNameOrEmpty() + getCurrentTime();
    }

    private static String getPlayerNameOrEmpty() {
        Player player = Player.getInstance();
        if(player == null || player.getName() == null || player.getName().isBlank()) {
            return "";
        }
        return "[" + player.getName() + "]";
    }

    private static String getCurrentTime() {
        LocalTime now = LocalTime.now();
        return "[" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + "]";
    }
}
