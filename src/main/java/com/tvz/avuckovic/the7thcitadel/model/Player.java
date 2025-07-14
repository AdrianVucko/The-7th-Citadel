package com.tvz.avuckovic.the7thcitadel.model;

import com.tvz.avuckovic.the7thcitadel.component.GameLogger;
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

    public static Player getInstance() {
        return instance;
    }

    public static void copyAttributes(Player player) {
        instance.setName(player.getName());
        instance.setHealth(player.getHealth());
        instance.setMaxHealth(player.getMaxHealth());
        instance.setActionDeck(player.getActionDeck());
        instance.setDiscardPile(player.getDiscardPile());
    }

    public static Player create() {
        return new Player();
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
