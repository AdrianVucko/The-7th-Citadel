package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.chat.ChatRemoteService;
import com.tvz.avuckovic.the7thcitadel.chat.SharedLogService;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;

public class SharedUtils {

    private SharedUtils() {}

    public static void sendChatMessage(String chatMessage, ChatRemoteService chatRemoteService) {
        try {
            String playerName = Player.getInstance().getName();
            chatRemoteService.sendChatMessage("[" + playerName + "]: " + chatMessage);
        } catch (RemoteException e) {
            throw new ApplicationException("Error while sending a chat message!", e);
        }
    }

    public static void saveLogMessage(String message, SharedLogService sharedLogService) {
        try {
            sharedLogService.saveLog(message);
        } catch (RemoteException e) {
            throw new ApplicationException("Error while saving log message!", e);
        }
    }

    public static Timeline getChatTimeline(ChatRemoteService chatRemoteService, SharedLogService sharedLogService,
                                           TextArea chatTextArea, TextArea logArea) {
        Timeline chatMessagesTimeline = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
                try {
                    List<String> chatMessages = chatRemoteService.getAllChatMessages();
                    String chatMessagesString = String.join("\n", chatMessages);
                    chatTextArea.setText(chatMessagesString);

                    List<String> allLogs = sharedLogService.getAllLogs();
                    List<String> logsToStore = GameLogger.filterLogs(allLogs);
                    GameLogger.storeNewLogs(logsToStore);
                    String logsString = String.join("\n", logsToStore);
                    logArea.appendText(logsString);
                    if(!logsToStore.isEmpty()) {
                        logArea.appendText("\n");
                    }
                } catch (RemoteException e) {
                    throw new ConfigurationException("An error occured while creating the timeline for chat and log!", e);
                }
        }), new KeyFrame(Duration.seconds(1)));
        chatMessagesTimeline.setCycleCount(Animation.INDEFINITE);
        return chatMessagesTimeline;
    }
}
