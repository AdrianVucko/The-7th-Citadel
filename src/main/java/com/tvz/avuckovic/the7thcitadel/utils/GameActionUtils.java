package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.controller.ActionDisplayController;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.ExplorationArea;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.model.SkillType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameActionUtils {
    public static AnchorPane createDisplay(GameAction action) {
        try {
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource("fxml/action-display.fxml"));
            AnchorPane pane = loader.load();
            ActionDisplayController controller = loader.getController();
            controller.setAction(action);
            return pane;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<GameAction> loadGameActions() {
        if(!FileUtils.fileExists("dat/game_actions.ser")) {
            return saveGameActions();
        }
        return FileUtils.loadObjectsFromFile("dat/game_actions.ser");
    }

    public static Map<ExplorationArea, List<GameAction>> distributeActions(List<GameAction> actions) {
        List<GameAction> shuffled = new ArrayList<>(actions);
        Collections.shuffle(shuffled);

        ExplorationArea[] areas = ExplorationArea.values();
        Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea = new EnumMap<>(ExplorationArea.class);

        for (ExplorationArea area : areas) {
            actionsPerExplorationArea.put(area, new ArrayList<>());
        }

        for (int i = 0; i < shuffled.size(); i++) {
            ExplorationArea area = areas[i % areas.length];
            actionsPerExplorationArea.get(area).add(shuffled.get(i));
        }

        return actionsPerExplorationArea;
    }

    public static GameAction selectRandomActionPerExplorationArea(
            Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea) {
        List<GameAction> actions = actionsPerExplorationArea.values().stream()
                .flatMap(Collection::stream).collect(Collectors.toList());
        Collections.shuffle(actions);
        if(actions.isEmpty()) {
            throw new ApplicationException("No game actions was chosen for winning!");
        }
        return actions.get(0);
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
