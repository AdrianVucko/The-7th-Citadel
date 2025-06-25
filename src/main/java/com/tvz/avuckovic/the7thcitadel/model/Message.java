package com.tvz.avuckovic.the7thcitadel.model;

import lombok.Getter;

@Getter
public enum Message {
    START_APPLICATION_ERROR_MESSAGE("A player type (SINGLE_PLAYER, PLAYER_ONE, PLAYER_TWO) must be provided!"),
    PLAYER_WILL_DROWN_WARNING("You are going to drown if you go to water again!"),
    PLAYER_DROWNED("You drowned in water!"),
    NO_UNUSED_CARDS("No unused cards available!"),
    SKILL_WASTED("Your skill is wasted because health is already at max!"),
    END("You died!");

    private final String text;

    Message(String text) {
        this.text = text;
    }
}
