package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.chat.ChatRemoteService;
import com.tvz.avuckovic.the7thcitadel.component.CardCell;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.component.GameMap;
import com.tvz.avuckovic.the7thcitadel.component.PlayerDisplay;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainController {
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

    public void init() {
        allCards = CardUtils.loadCards();
        initializePlayer();
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(Player.getInstance().getActionDeck());
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        initializeChatAndLog();
        initializeActionFields(null);
        Platform.runLater(this::saveFirstTimeForPlayerOne);
    }

    public void init(GameState gameState, GameMode gameMode) {
        allCards = CardUtils.loadCards();
        loadPlayer(gameState);
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(Player.getInstance().getActionDeck());
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        initializeChatAndLog();
        initializeActionFields(gameState);
        Platform.runLater(() -> {
            gameMap.markCompletedFields(gameState.getCompletedFields());
            if(!gameMode.equals(GameMode.REPLAY)) {
                gameMap.saveGameMove();
            }
        });
    }

    public void acquireSkill() {
        gameMap.checkMoveTurn();
        Player player = Player.getInstance();
        List<Card> allPlayerCards = new ArrayList<>();
        allPlayerCards.addAll(player.getActionDeck());
        allPlayerCards.addAll(player.getDiscardPile());
        Card unusedCard = CardUtils.selectUnusedRandomSkill(allCards, allPlayerCards);
        player.getActionDeck().add(unusedCard);
        cardsListView.getItems().add(unusedCard);
        decrementPlayerHealth();
        PlayerDisplay.fillPlayerLabels();
        gameMap.saveGameMove();
        sendMoveToOtherPlayer();
    }

    public void useSkill() {
        gameMap.checkMoveTurn();
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
            gameMap.saveGameMove();
            sendMoveToOtherPlayer();
        }, () -> GameLogger.warn(Message.NO_SKILL_SELECTED.getText()));
    }

    public void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (GamePlayThreadsUtils.isMultiplayer() && !message.isEmpty()) {
            SharedUtils.sendChatMessage(message, chatRemoteService);
        }
        messageInput.clear();
    }

    public void saveGame() {
        GameStateUtils.save(actionsPerExplorationArea, winningAction, gameMap.getCompletedFields());
    }

    public GameState replayedState() {
        GameMove replayedGameMove = GameMoveUtils.replayMove();
        return GameStateUtils.buildGameState(actionsPerExplorationArea, winningAction, replayedGameMove);
    }

    private void sendMoveToOtherPlayer() {
        if (GamePlayThreadsUtils.isMultiplayer()) {
            GameMove gameMove = gameMap.buildGameMove();
            GameState gameState = GameStateUtils.buildGameState(actionsPerExplorationArea, winningAction, gameMove);
            GamePlayThreadsUtils.sendMove(gameState, false);
        }
    }

    private void saveFirstTimeForPlayerOne() {
        if(GamePlayThreadsUtils.isMultiplayer() &&
                !TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.PLAYER_TWO)) {
            Player playerOne = Player.getInstance();
            Player playerTwo = Player.createFilledPlayer(allCards, false);
            GameMoveUtils.saveFirstTimeForPlayerOne(gameMap.getCompletedFields(), playerOne, playerTwo);

        } else if (GamePlayThreadsUtils.isMultiplayer()) {
            throw new ApplicationException("Player One needs to make first move for game to start");
        } else {
            gameMap.saveGameMove();
        }
    }

    private void initializeActionFields(GameState gameState) {
        boolean actionsDistributed;
        Optional<GameState> optionalGameState = Optional.ofNullable(gameState);
        List<GameAction> gameActions = GameActionUtils.loadGameActions();
        do {
            actionsPerExplorationArea = optionalGameState.map(GameState::getActionsPerExplorationArea)
                    .orElse(GameActionUtils.distributeActions(gameActions));
            winningAction = optionalGameState.map(GameState::getWinningAction)
                    .orElse(GameActionUtils.selectRandomActionPerExplorationArea(actionsPerExplorationArea));
            actionsDistributed = gameMap.distributeActions(actionsPerExplorationArea, winningAction);
        } while (!actionsDistributed);
    }

    private Optional<Card> getSelectedCard() {
        Card selectedItem = cardsListView.getSelectionModel().getSelectedItem();
        return Optional.ofNullable(selectedItem);
    }

    private void loadPlayer(GameState gameState) {
        PlayerDisplay.attach(playerName, playerHealth, cardsListView);
        Player.copyAttributes(gameState);
        PlayerDisplay.fillPlayerLabels();
    }

    private void initializePlayer() {
        PlayerDisplay.attach(playerName, playerHealth, cardsListView);
        Player playerOne = Player.createFilledPlayer(allCards, true);
        Player.copyAttributes(playerOne);
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
        if (GamePlayThreadsUtils.isMultiplayer()) {
            chatRemoteService = SharedUtils.initializeChatAndLogTimeline(chatArea, gameLog);
        } else {
            GameLogger.attach(gameLog);
        }
    }
}