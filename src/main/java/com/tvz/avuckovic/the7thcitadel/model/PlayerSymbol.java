package com.tvz.avuckovic.the7thcitadel.model;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;

public enum PlayerSymbol {
    ONE, TWO;

    public static Player evaluatePlayerInvolved(GameMove gameMove) {
        if(isPlayerTwo()) {
            return gameMove.getPlayerTwo();
        }
        return gameMove.getPlayerOne();
    }

    public static PlayerSymbol getAssignedPlayerSymbol() {
        if (isPlayerTwo()) {
            return PlayerSymbol.TWO;
        }
        return PlayerSymbol.ONE;
    }

    private static boolean isPlayerTwo() {
        return TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.PLAYER_TWO);
    }
}
