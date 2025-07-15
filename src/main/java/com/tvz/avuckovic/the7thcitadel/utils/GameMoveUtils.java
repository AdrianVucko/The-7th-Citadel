package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.exception.ConfigurationException;
import com.tvz.avuckovic.the7thcitadel.model.GameMove;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import com.tvz.avuckovic.the7thcitadel.model.PlayerType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMoveUtils {
    public static GameMove replayMove() {
        if(!FileUtils.fileExists(XmlUtils.GAME_MOVES_XML_FILE_NAME)) {
            throw new ApplicationException("File that contains moves doesn't exist");
        }
        List<GameMove> gameMoves = XmlUtils.readGameMovesFromXmlFile();
        if (gameMoves.size() < 2) {
            throw new ApplicationException("You need to make a move first");
        }
        List<GameMove> replayedGameMoves = gameMoves.subList(0, gameMoves.size() - 1);
        XmlUtils.saveGameMovesToXmlFile(replayedGameMoves);
        return replayedGameMoves.get(replayedGameMoves.size() - 1);
    }

    public static void saveFirstTimeForPlayerOne(List<Integer> completedFields, Player playerOne, Player playerTwo) {
        GameMove gameMove = null;
        if(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.SINGLE_PLAYER)) {
            gameMove = GameMove.builder()
                    .playerOne(playerOne)
                    .playerTwo(null)
                    .completedFields(completedFields)
                    .build();
        }
        if(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.PLAYER_ONE)) {
            gameMove = GameMove.builder()
                    .playerOne(playerOne)
                    .playerTwo(playerTwo)
                    .completedFields(completedFields)
                    .build();
        }
        if(TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.PLAYER_TWO)) {
            throw new ConfigurationException("Player two not allowed for first move");
        }
        saveGameMove(gameMove);
    }

    public static void saveGameMove(GameMove gameMove) {
        List<GameMove> gameMoves = new ArrayList<>();
        if(FileUtils.fileExists(XmlUtils.GAME_MOVES_XML_FILE_NAME)) {
            gameMoves.addAll(XmlUtils.readGameMovesFromXmlFile());
        }
        gameMoves.add(gameMove);
        XmlUtils.saveGameMovesToXmlFile(gameMoves);
    }

    public static void clearMoves() {
        if(FileUtils.fileExists(XmlUtils.GAME_MOVES_XML_FILE_NAME)) {
            boolean deleted = FileUtils.deleteFile(XmlUtils.GAME_MOVES_XML_FILE_NAME);
            if(!deleted) {
                throw new ConfigurationException("Deleting game moves failed");
            }
        }
    }
}
