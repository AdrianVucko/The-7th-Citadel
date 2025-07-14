package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.model.Card;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerDisplay {
    private static Label playerName;
    private static Label playerHealth;
    private static ListView<Card> playerCards;

    public static void attach(Label name, Label health, ListView<Card> cardsListView) {
        playerName = name;
        playerHealth = health;
        playerCards = cardsListView;
    }

    public static void fillPlayerLabels() {
        Player player = Player.getInstance();
        playerName.setText("Name: " + player.getName());
        playerHealth.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    public static void removeCardsFromView(List<Card> discardCards) {
        for (Card discardCard : discardCards) {
            playerCards.getItems().remove(discardCard);
        }
    }
}
