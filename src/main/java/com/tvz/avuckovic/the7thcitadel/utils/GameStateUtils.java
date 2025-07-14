package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStateUtils {
    public static void save(Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea,
                            GameAction winningAction, List<Integer> completedFields) {
        GameState gameState = buildGameState(actionsPerExplorationArea, winningAction, completedFields);
        FileUtils.writeObject("dat/savedGame.ser", gameState);
    }

    public static GameState buildGameState(Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea,
                                           GameAction winningAction, List<Integer> completedFields) {
        Player playerOne = Player.getInstance();
        return GameState.builder()
                .playerOne(playerOne)
                .playerTwo(null)
                .actionsPerExplorationArea(actionsPerExplorationArea)
                .winningAction(winningAction)
                .completedFields(completedFields)
                .build();
    }

    public static GameState buildGameState(Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea,
                                           GameAction winningAction, GameMove gameMove) {
        return GameState.builder()
                .playerOne(gameMove.getPlayerOne())
                .playerTwo(gameMove.getPlayerTwo())
                .actionsPerExplorationArea(actionsPerExplorationArea)
                .winningAction(winningAction)
                .completedFields(gameMove.getCompletedFields())
                .build();
    }

    public static GameMove buildGameMove(List<Integer> completedFields) {
        Player playerOne = Player.getInstance();
        return GameState.builder()
                .playerOne(playerOne)
                .playerTwo(null)
                .completedFields(completedFields)
                .build();
    }

    public static GameState loadGame() {
        if(FileUtils.fileExists("dat/savedGame.ser")) {
            return FileUtils.loadObjectFromFile("dat/savedGame.ser");
        }
        throw new ApplicationException("Game needs to be saved first");
    }
}
