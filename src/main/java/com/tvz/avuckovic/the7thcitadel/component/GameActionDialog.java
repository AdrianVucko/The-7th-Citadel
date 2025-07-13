package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.controller.ActionDisplayController;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameActionDialog extends Dialog<Boolean> {
    private boolean diceRolled = false;
    private boolean cardsSubmitted = false;
    private boolean taskFailed = false;
    private boolean finishedProperly = false;
    private final ExplorationArea winningArea;
    private final ExplorationArea actionArea;
    private final GameAction action;
    private final Label diceResultLabel = new Label();
    private final Label skillResultLabel = new Label();
    private final Button throwDiceButton;
    private final Button submitCardsButton;
    private final Button finishButton;

    public GameActionDialog(ExplorationArea winningArea, ExplorationArea actionArea, GameAction action) {
        this.winningArea = winningArea;
        this.actionArea = actionArea;
        this.action = action;

        setTitle("Resolve Action");
        setHeaderText("Choose how to proceed");

        try {
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource("fxml/action-display.fxml"));
            AnchorPane content = loader.load();
            ActionDisplayController controller = loader.getController();
            controller.setAction(this.action);
            getDialogPane().setContent(content);
        } catch (Exception e) {
            e.printStackTrace();
            getDialogPane().setContent(new Label("Failed to load action display."));
        }

        VBox wrapper = new VBox(5);
        wrapper.getChildren().addAll(getDialogPane().getContent(), diceResultLabel, skillResultLabel);
        getDialogPane().setContent(wrapper);

        ButtonType giveUpType = new ButtonType("Give Up", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType throwDiceType = new ButtonType("Throw Dice", ButtonBar.ButtonData.OTHER);
        ButtonType submitCardsType = new ButtonType("Submit Cards", ButtonBar.ButtonData.OTHER);
        ButtonType finishType = new ButtonType("Finish Action", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(giveUpType, throwDiceType, submitCardsType, finishType);

        throwDiceButton = (Button) getDialogPane().lookupButton(throwDiceType);
        submitCardsButton = (Button) getDialogPane().lookupButton(submitCardsType);
        finishButton = (Button) getDialogPane().lookupButton(finishType);

        throwDiceButton.addEventFilter(ActionEvent.ACTION, this::throwDice);
        submitCardsButton.addEventFilter(ActionEvent.ACTION, this::submitCards);
        finishButton.addEventFilter(ActionEvent.ACTION, event -> performFinishAction());
        finishButton.setDisable(true);

        setOnHidden(event -> giveUp());
        setResultConverter((buttonType) -> getActionCompleted());
    }

    private void giveUp() {
        if (finishedProperly) {
           return;
        }

        Player player = Player.getInstance();
        int healthLost = 2;
        if (taskFailed && action.healthLoss() > 2) {
            healthLost = action.healthLoss();
        }
        player.modifyHealth(-healthLost);
        GameLogger.info(String.format(Message.FAILED_ACTION.getText(), healthLost));
        PlayerDisplay.fillPlayerLabels();

        if(player.getHealth() <= 0) {
            throw new ApplicationException(Message.END.getText());
        }
    }

    private void performFinishAction() {
        Player player = Player.getInstance();

        if(taskFailed) {
            player.modifyHealth(-this.action.healthLoss());
            GameLogger.info(String.format(Message.FAILED_ACTION.getText(), this.action.healthLoss()));
            if(player.getHealth() <= 0) {
                throw new ApplicationException(Message.END.getText());
            }
        } else {
            player.modifyHealth(this.action.healthGain());
            GameLogger.info(String.format(Message.ACTION_FINISHED.getText(), this.actionArea.getFullDescription()));
            logExplorationArea();
        }
        PlayerDisplay.fillPlayerLabels();
        finishedProperly = true;
    }

    private void throwDice(ActionEvent event) {
        diceRolled = true;
        int diceCount = 3;
        int totalStars = 0;
        StringBuilder resultDisplay = new StringBuilder("🎲 Dice roll: ");

        for (int i = 0; i < diceCount; i++) {
            int roll = new java.util.Random().nextInt(3); // 0–2 stars per die
            totalStars += roll;
            resultDisplay.append("[").append("⭐".repeat(roll)).append("] ");
        }

        resultDisplay.append("→ ").append(totalStars).append("⭐ total");
        diceResultLabel.setText(resultDisplay.toString());
        GameLogger.info(resultDisplay.toString());

        if(totalStars < this.action.pointsNeeded()) {
            taskFailed = true;
        }

        throwDiceButton.setDisable(true);
        updateFinishButtonState();
        event.consume();
    }

    private void submitCards(ActionEvent event) {
        cardsSubmitted = true;

        List<Card> discarded = CardUtils.removeSkillCardsFromPlayer(action.requiredSkill(), action.skillsNeeded());
        PlayerDisplay.removeCardsFromView(discarded);
        if(discarded.size() < action.skillsNeeded()) {
            taskFailed = true;
        }

        String submitMessage = String.format(Message.CARDS_SUBMITTED.getText(),
                discarded.size(), action.skillsNeeded(), action.requiredSkill().name().toLowerCase());
        skillResultLabel.setText(submitMessage);
        GameLogger.info(submitMessage);

        submitCardsButton.setDisable(true);
        updateFinishButtonState();
        event.consume();
    }

    private void logExplorationArea() {
        int winningAreaNumber = CardUtils.evaluateAreaNumberByExplorationArea(winningArea);
        int actionAreaNumber = CardUtils.evaluateAreaNumberByExplorationArea(actionArea);
        if(actionAreaNumber < winningAreaNumber) {
            GameLogger.info("\uD83D\uDDFA Final task is in a bigger exploration area");
        } else if (actionAreaNumber == winningAreaNumber) {
            GameLogger.info("\uD83D\uDDFA Final task is located in this exploration area");
        } else {
            GameLogger.info("\uD83D\uDDFA Final task is in a smaller exploration area");
        }
    }

    private void updateFinishButtonState() {
        finishButton.setDisable(!(diceRolled && cardsSubmitted));
    }

    private Boolean getActionCompleted() {
        return !taskFailed && finishedProperly;
    }
}