package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.controller.ActionDisplayController;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class GameActionDialog extends Dialog<Void> {

    private boolean diceRolled = false;
    private boolean cardsSubmitted = false;
    private final Label diceResultLabel = new Label();
    private final Button finishButton;

    public GameActionDialog(GameAction action) {
        setTitle("Resolve Action");
        setHeaderText("Choose how to proceed");

        try {
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource("fxml/action-display.fxml"));
            AnchorPane content = loader.load();
            ActionDisplayController controller = loader.getController();
            controller.setAction(action);
            getDialogPane().setContent(content);
        } catch (Exception e) {
            e.printStackTrace();
            getDialogPane().setContent(new Label("Failed to load action display."));
        }

        VBox wrapper = new VBox(10);
        wrapper.getChildren().addAll(getDialogPane().getContent(), diceResultLabel);
        getDialogPane().setContent(wrapper);

        ButtonType giveUpType = new ButtonType("Give Up", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType throwDiceType = new ButtonType("Throw Dice", ButtonBar.ButtonData.OTHER);
        ButtonType submitCardsType = new ButtonType("Submit Cards", ButtonBar.ButtonData.OTHER);
        ButtonType finishType = new ButtonType("Finish Action", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(giveUpType, throwDiceType, submitCardsType, finishType);

        Button throwDiceButton = (Button) getDialogPane().lookupButton(throwDiceType);
        Button submitCardsButton = (Button) getDialogPane().lookupButton(submitCardsType);
        finishButton = (Button) getDialogPane().lookupButton(finishType);
        finishButton.setDisable(true);

        throwDiceButton.addEventFilter(ActionEvent.ACTION, event -> {
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
            throwDiceButton.setDisable(true);
            updateFinishButtonState();
            event.consume();
        });

        submitCardsButton.addEventFilter(ActionEvent.ACTION, event -> {
            cardsSubmitted = true;
            GameLogger.info("📦 Cards submitted!");
            updateFinishButtonState();
            event.consume();
        });
    }

    private void updateFinishButtonState() {
        finishButton.setDisable(!(diceRolled && cardsSubmitted));
    }
}