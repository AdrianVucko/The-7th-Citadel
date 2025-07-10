package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.component.CardCell;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.component.GameMap;
import com.tvz.avuckovic.the7thcitadel.component.PlayerDisplay;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import com.tvz.avuckovic.the7thcitadel.utils.GameActionUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private List<Card> allCards;
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    @FXML public Label playerName;
    @FXML public Label playerHealth;
    @FXML public StackPane gameBoard;
    @FXML public TextArea gameLog;
    @FXML public ListView<Card> cardsListView;
    @FXML public GameMap gameMap;
    @FXML public Pane progressDraw;
    @FXML public ListView<?> inventoryList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GameLogger.attach(gameLog);
        allCards = CardUtils.loadCards();
        List<GameAction> gameActions = GameActionUtils.loadGameActions();
        List<Card> playerCards = CardUtils.drawShuffledActionCards(allCards);
        actionsPerExplorationArea = GameActionUtils.distributeActions(gameActions);
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(playerCards);
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        gameMap.distributeActions(actionsPerExplorationArea);
        PlayerDisplay.attach(playerName, playerHealth, cardsListView);
        initializePlayer(playerCards);
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

    private Optional<Card> getSelectedCard() {
        Card selectedItem = cardsListView.getSelectionModel().getSelectedItem();
        return Optional.ofNullable(selectedItem);
    }

    private void initializePlayer(List<Card> playerCards) {
        Player player = Player.getInstance();
        player.setName("Arthen");
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
}