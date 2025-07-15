package com.tvz.avuckovic.the7thcitadel.model;

import lombok.Getter;

@Getter
public enum Message {
    START_APPLICATION_ERROR_MESSAGE("A player type (SINGLE_PLAYER, PLAYER_ONE, PLAYER_TWO) must be provided!"),
    PLAYER_WILL_DROWN_WARNING("You are going to drown if you go to water again!"),
    PLAYER_DROWNED("You drowned in water!"),
    NO_UNUSED_CARDS("No unused cards available!"),
    NO_SKILL_SELECTED("No skill selected!"),
    SKILL_WASTED("Your skill is wasted because health is already at max!"),
    CARDS_SUBMITTED("📦 %d of %d %s cards submitted!"),
    ACTION_FINISHED("✅ Action completed successfully in exploration area '%s'!"),
    FAILED_ACTION("\uD83D\uDEAA Failed action and lost %d health points!"),
    GAME_WON("You have won!!! Congratulations!"),
    END("You died!"),
    ALREADY_DEAD("You can't make moves because you are dead!"),
    NOT_YOUR_MOVE("Not your move yet"),
    SOMETHING_HAPPENED("Something happened"),
    SUPPORT("Error occurred while setting up application! Contact support"),
    UNKNOWN_ERROR("An unknown error occurred.");

    private final String text;

    Message(String text) {
        this.text = text;
    }
}
