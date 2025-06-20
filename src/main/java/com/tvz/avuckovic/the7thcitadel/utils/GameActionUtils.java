package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.model.SkillType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameActionUtils {
    public static List<GameAction> loadGameActions() {
        if(!FileUtils.fileExists("dat/game_actions.ser")) {
            return saveGameActions();
        }
        return FileUtils.loadObjectsFromFile("dat/game_actions.ser");
    }

    private static List<GameAction> saveGameActions() {
        List<GameAction> gameActions = FileUtils
                .readRowAttributesForFile("dat/game_actions.txt", true).stream()
                .map(GameActionUtils::buildGameActionsFromAttributes)
                .collect(Collectors.toList());
        FileUtils.writeObjects("dat/game_actions.ser", gameActions);
        return gameActions;
    }

    private static GameAction buildGameActionsFromAttributes(String[] actionAttributes) {
        if(actionAttributes.length != 6) {
            throw new IllegalStateException("action data not split correctly");
        }
        String description = actionAttributes[0];
        SkillType skillType = SkillType.valueOf(actionAttributes[1].toUpperCase());
        int cardsNeeded = Integer.parseInt(actionAttributes[2]);
        int pointsNeeded = Integer.parseInt(actionAttributes[3]);
        int healthGain = Integer.parseInt(actionAttributes[4]);
        int healthLoss = Integer.parseInt(actionAttributes[5]);
        return new GameAction(description, skillType, cardsNeeded, pointsNeeded, healthGain, healthLoss);
    }
}
