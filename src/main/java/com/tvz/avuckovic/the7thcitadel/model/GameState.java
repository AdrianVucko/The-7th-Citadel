package com.tvz.avuckovic.the7thcitadel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GameState implements Serializable {
    private Player playerOne;
    private Player playerTwo;
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    private Map.Entry<ExplorationArea, GameAction> winningAction;
    private List<Integer> completedFields;
}
