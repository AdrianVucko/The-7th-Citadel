package com.tvz.avuckovic.the7thcitadel.controller;


import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.model.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public class CardCellController {

    @FXML private ImageView typeIcon;
    @FXML private Label descriptionLabel;
    @FXML private Label flagIndicator;
    @FXML private AnchorPane root;

    public void setCard(Card card) {
        descriptionLabel.setText(card.getDescription());

        String iconPath = "images/type_" + card.getType().name().toLowerCase() + ".png";
        typeIcon.setImage(new Image(Objects.requireNonNull(RootController.class.getResourceAsStream(iconPath))));

        String bgColor = switch (card.getBackColor()) {
            case NONE -> "#ADD8E6"; // light blue
            case GOLD -> "#FFD700";
            case GREEN -> "#90EE90";
        };
        root.setStyle("-fx-background-color: " + bgColor + ";"
                + "-fx-border-color: black; -fx-background-radius: 8; -fx-border-radius: 8;");

        String flagSymbol = switch (card.getFlag()) {
            case NONE -> "";
            case SCENARIO -> "⚑";
            case EMPTY -> "🚩";
        };
        flagIndicator.setText(flagSymbol);
    }
}
