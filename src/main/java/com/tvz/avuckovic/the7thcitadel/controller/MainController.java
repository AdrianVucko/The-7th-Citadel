package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.component.CardCell;
import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.component.GameMap;
import com.tvz.avuckovic.the7thcitadel.model.Card;
import com.tvz.avuckovic.the7thcitadel.model.ExplorationArea;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.utils.GameActionUtils;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private List<Card> playerCards;
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    @FXML public StackPane gameBoard;
    @FXML public TextArea gameLog;
    @FXML public ListView<Card> cardsListView;
    @FXML public GameMap gameMap;
    @FXML public Pane progressDraw;
    @FXML public ListView<?> inventoryList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GameLogger.attach(gameLog);
        List<Card> allCards = CardUtils.loadCards();
        List<GameAction> gameActions = GameActionUtils.loadGameActions();
        playerCards = CardUtils.drawShuffledActionCards(allCards);
        actionsPerExplorationArea = GameActionUtils.distributeActions(gameActions);
        cardsListView.setCellFactory(list -> new CardCell());
        cardsListView.getItems().setAll(playerCards);
        progressDraw.setPickOnBounds(false);
        gameMap.connectComponents(progressDraw);
        gameMap.distributeActions(actionsPerExplorationArea);
    }

    public void useItem() {
        getSelectedCard().ifPresentOrElse(card -> {
            String logEntry = String.format("🃏 Card Used — [ID: %s] \"%s\"", card.getId(), card.getDescription());
            GameLogger.info(logEntry);
        }, () -> GameLogger.warn("No card selected. Please choose a card first."));
    }

    private Optional<Card> getSelectedCard() {
        Card selectedItem = cardsListView.getSelectionModel().getSelectedItem();
        return Optional.ofNullable(selectedItem);
    }
}