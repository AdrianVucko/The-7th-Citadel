package com.tvz.avuckovic.the7thcitadel.model;

import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import com.tvz.avuckovic.the7thcitadel.utils.GamePlayThreadsUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Player implements Serializable {
    private static final Player instance = new Player();

    private String name;
    private List<Card> actionDeck = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private int health;
    private int maxHealth;
    private boolean onMove;

    public static Player getInstance() {
        return instance;
    }

    public static void copyAttributes(Player player) {
        copyAttributesInternal(player);
    }

    public static void copyAttributes(GameState gameState) {
        Player player = PlayerSymbol.evaluatePlayerInvolved(gameState);
        copyAttributesInternal(player);
    }

    private static void copyAttributesInternal(Player player) {
        instance.setName(player.getName());
        instance.setHealth(player.getHealth());
        instance.setMaxHealth(player.getMaxHealth());
        instance.setActionDeck(player.getActionDeck());
        instance.setDiscardPile(player.getDiscardPile());
        if (GamePlayThreadsUtils.isMultiplayer()) {
            instance.setOnMove(player.isOnMove());
        } else {
            instance.setOnMove(true);
        }
    }

    public static Player create() {
        return new Player();
    }

    public static Player createFilledPlayer(List<Card> allCards, boolean onMove) {
        Player player = new Player();
        List<Card> playerCards = CardUtils.drawShuffledActionCards(allCards);
        player.setName(CardUtils.assignPlayerName(allCards));
        player.setHealth(GameConstants.Player.START_HEALTH);
        player.setMaxHealth(GameConstants.Player.MAX_HEALTH);
        player.getActionDeck().addAll(playerCards);
        if (GamePlayThreadsUtils.isMultiplayer()) {
            player.setOnMove(onMove);
        } else {
            player.setOnMove(true);
        }
        return player;
    }

    public void modifyHealth(int delta) {
        this.health += delta;
        if(this.health < 0) {
            this.health = 0;
        } else if (this.health > this.maxHealth) {
            this.health = maxHealth;
        }
        GameLogger.info("Health changed to " + this.health);
    }
}
