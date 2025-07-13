package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.chat.ChatRemoteService;
import com.tvz.avuckovic.the7thcitadel.chat.SharedLogService;
import com.tvz.avuckovic.the7thcitadel.component.CardCell;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.component.GameMap;
import com.tvz.avuckovic.the7thcitadel.component.PlayerDisplay;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationKey;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationReader;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import com.tvz.avuckovic.the7thcitadel.utils.SharedUtils;
import com.tvz.avuckovic.the7thcitadel.utils.GameActionUtils;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class MainController implements Initializable {
    @FXML public TextArea chatArea;
    @FXML public TextField messageInput;
    @FXML public Label playerName;
    @FXML public Label playerHealth;
    @FXML public StackPane gameBoard;
    @FXML public TextArea gameLog;
    @FXML public ListView<Card> cardsListView;
    @FXML public GameMap gameMap;
    @FXML public Pane progressDraw;
    private ChatRemoteService chatRemoteService;
    private List<Card> allCards;
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    private GameAction winningAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allCards = CardUtils.loadCards();
        List<Card> playerCards = CardUtils.drawShuffledActionCards(allCards);
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(playerCards);
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        initializePlayer(playerCards);
        initializeChatAndLog();
        initializeActionFields();
    }

    public void acquireSkill() {
        Player player = Player.getInstance();
        List<Card> allPlayerCards = new ArrayList<>();
        allPlayerCards.addAll(player.getActionDeck());
        allPlayerCards.addAll(player.getDiscardPile());
        Card unusedCard = CardUtils.selectUnusedRandomSkill(allCards, allPlayerCards);
        player.getActionDeck().add(unusedCard);
        cardsListView.getItems().add(unusedCard);
        decrementPlayerHealth();
        PlayerDisplay.fillPlayerLabels();
    }

    public void useSkill() {
        getSelectedCard().ifPresentOrElse(card -> {
            String logEntry = String.format("🃏 Skill Used — [ID: %s] \"%s\"", card.getId(), card.getDescription());
            GameLogger.info(logEntry);

            Player player = Player.getInstance();
            player.getActionDeck().remove(card);
            player.getDiscardPile().add(card);

            cardsListView.getItems().remove(card);
            cardsListView.getSelectionModel().clearSelection();

            incrementPlayerHealth();
            PlayerDisplay.fillPlayerLabels();
        }, () -> GameLogger.warn(Message.NO_SKILL_SELECTED.getText()));
    }

    public void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (isMultiplayer() && !message.isEmpty()) {
            SharedUtils.sendChatMessage(message, chatRemoteService);
        }
        messageInput.clear();
    }

    private void initializeActionFields() {
        boolean actionsDistributed;
        do {
            List<GameAction> gameActions = GameActionUtils.loadGameActions();
            actionsPerExplorationArea = GameActionUtils.distributeActions(gameActions);
            winningAction = GameActionUtils.selectRandomActionPerExplorationArea(actionsPerExplorationArea);
            actionsDistributed = gameMap.distributeActions(actionsPerExplorationArea, winningAction);
        } while (!actionsDistributed);
    }

    private Optional<Card> getSelectedCard() {
        Card selectedItem = cardsListView.getSelectionModel().getSelectedItem();
        return Optional.ofNullable(selectedItem);
    }

    private void initializePlayer(List<Card> playerCards) {
        PlayerDisplay.attach(playerName, playerHealth, cardsListView);
        Player player = Player.getInstance();
        player.setName(CardUtils.assignPlayerName(allCards));
        player.setHealth(GameConstants.Player.START_HEALTH);
        player.setMaxHealth(GameConstants.Player.MAX_HEALTH);
        player.getActionDeck().addAll(playerCards);
        PlayerDisplay.fillPlayerLabels();
    }

    private void incrementPlayerHealth() {
        Player player = Player.getInstance();
        if(player.getHealth() >= player.getMaxHealth()) {
            throw new ApplicationException(Message.SKILL_WASTED.getText());
        }
        player.modifyHealth(1);
    }

    private void decrementPlayerHealth() {
        Player player = Player.getInstance();
        player.modifyHealth(-1);
        if(player.getHealth() <= 0) {
            PlayerDisplay.fillPlayerLabels();
            throw new ApplicationException(Message.END.getText());
        }
    }

    private void initializeChatAndLog() {
        if (isMultiplayer()) {
            try {
                Registry registry = LocateRegistry.getRegistry(
                        ConfigurationReader.getStringValue(ConfigurationKey.HOSTNAME),
                        ConfigurationReader.getIntegerValue(ConfigurationKey.RMI_PORT));
                chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_OBJECT_NAME);
                SharedLogService sharedLogService = (SharedLogService) registry.lookup(SharedLogService.REMOTE_OBJECT_NAME);

                GameLogger.attach(sharedLogService);
                Timeline chatMessagesTimeline = SharedUtils.getChatTimeline(chatRemoteService, sharedLogService,
                        chatArea, gameLog);
                chatMessagesTimeline.play();

            } catch (RemoteException | NotBoundException e) {
                throw new ConfigurationException("An error occured while initializing the chat middleware!", e);
            }
        } else {
            GameLogger.attach(gameLog);
        }
    }

    private static boolean isMultiplayer() {
        return !TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.SINGLE_PLAYER);
    }
}