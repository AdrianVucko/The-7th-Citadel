package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.model.GameState;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import com.tvz.avuckovic.the7thcitadel.model.PlayerType;
import com.tvz.avuckovic.the7thcitadel.thread.PlayerOneMoveThread;
import com.tvz.avuckovic.the7thcitadel.thread.PlayerTwoMoveThread;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GamePlayThreadsUtils {
    public static void sendMove(GameState currentGameState, boolean nextTurn) {
        if(nextTurn) {
            Player playerOne = currentGameState.getPlayerOne();
            Player playerTwo = currentGameState.getPlayerTwo();
            playerOne.setOnMove(!playerOne.isOnMove());
            playerTwo.setOnMove(!playerTwo.isOnMove());
        }

        if (TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name().equals(
                PlayerType.PLAYER_ONE.name()))
        {
            PlayerOneMoveThread playerOneMoveThread = new PlayerOneMoveThread(currentGameState);
            Thread thread = new Thread(playerOneMoveThread);
            thread.start();
        } else if (TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().name().equals(
                PlayerType.PLAYER_TWO.name()))
        {
            PlayerTwoMoveThread playerTwoMoveThread = new PlayerTwoMoveThread(currentGameState);
            Thread thread = new Thread(playerTwoMoveThread);
            thread.start();
        }
    }

    public static boolean isMultiplayer() {
        return !TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.SINGLE_PLAYER);
    }
}
