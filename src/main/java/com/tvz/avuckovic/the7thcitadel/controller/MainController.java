package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.component.CardCell;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.component.GameMap;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.GameActionUtils;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
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
        initializePlayer(playerCards);
        actionsPerExplorationArea = GameActionUtils.distributeActions(gameActions);
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(playerCards);
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        gameMap.distributeActions(actionsPerExplorationArea);
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
        fillPlayerLabels();
    }

    public void useSkill() {
        getSelectedCard().ifPresentOrElse(card -> {
            String logEntry = String.format("🃏 Skill Used — [ID: %s] \"%s\"", card.getId(), card.getDescription());
            GameLogger.info(logEntry);

            Player player = Player.getInstance();
            player.getActionDeck().remove(card);
            player.getDiscardPile().add(card);
            incrementPlayerHealth();

            cardsListView.getItems().remove(card);
            cardsListView.getSelectionModel().clearSelection();
            fillPlayerLabels();
        }, () -> GameLogger.warn("No skill selected. Please choose one first."));
    }

    private Optional<Card> getSelectedCard() {
        Card selectedItem = cardsListView.getSelectionModel().getSelectedItem();
        return Optional.ofNullable(selectedItem);
    }

    private void initializePlayer(List<Card> playerCards) {
        Player player = Player.getInstance();
        player.setName("Arthen");
        player.setHealth(8);
        player.setMaxHealth(10);
        player.getActionDeck().addAll(playerCards);
        fillPlayerLabels();
    }

    private void fillPlayerLabels() {
        Player player = Player.getInstance();
        playerName.setText("Name: " + player.getName());
        playerHealth.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
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
            throw new ApplicationException(Message.END.getText());
        }
    }
}