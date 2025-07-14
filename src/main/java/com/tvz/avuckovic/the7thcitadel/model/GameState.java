package com.tvz.avuckovic.the7thcitadel.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Setter
@Getter
public class GameState extends GameMove implements Serializable {
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    private GameAction winningAction;

    public GameState(Player playerOne, Player playerTwo, List<Integer> completedFields,
                     Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea, GameAction winningAction) {
        super(playerOne, playerTwo, completedFields);
        this.actionsPerExplorationArea = actionsPerExplorationArea;
        this.winningAction = winningAction;
    }
}
