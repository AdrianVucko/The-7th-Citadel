package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.model.ExplorationArea;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.utils.DialogUtils;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Getter
public class Field extends StackPane {
    private final int row;
    private final int col;
    private boolean revealed;
    private GameAction action;

    public Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.revealed = false;

        setPrefSize(100, 100);
        setStyle("-fx-background-color: transparent;");
    }

    public boolean isFieldInWater() {
        int cellNumber = getCellNumber();
        return Arrays.stream(GameConstants.Board.WATER_FIELDS).anyMatch(waterField -> waterField == cellNumber);
    }

    public void assignAction(GameAction gameAction) {
        action = gameAction;
    }

    public void triggerAction() {
        if(action != null) {
            DialogUtils.showActionDialog(action);
        }
    }

    public ExplorationArea getExplorationArea() {
        int areaNumber = evaluateAreaNumber();
        return switch (areaNumber) {
            case 0 -> ExplorationArea.FIRST;
            case 1 -> ExplorationArea.SECOND;
            case 2 -> ExplorationArea.THIRD;
            case 3 -> ExplorationArea.FOURTH;
            case 4 -> ExplorationArea.FIFTH;
            case 5 -> ExplorationArea.SIXTH;
            case 6 -> ExplorationArea.SEVENTH;
            case 7 -> ExplorationArea.EIGHT;
            case 8 -> ExplorationArea.NINTH;
            case 9 -> ExplorationArea.TENTH;
            default -> ExplorationArea.NONE;
        };
    }

    public int getCellNumber() {
        return getRow() * GameConstants.Board.ROWS + getCol();
    }

    private int evaluateAreaNumber() {
        BigDecimal numberOfFields = new BigDecimal(GameConstants.Board.ROWS * GameConstants.Board.COLS);
        int split = numberOfFields.divide(new BigDecimal("10"), 0, RoundingMode.FLOOR).intValue();
        for(int i = 0; i < 10; i++) {
            if(getCellNumber() < ((i+1) * split)) {
                return i;
            }
        }
        return 10;
    }

    public void markAsRevealed() {
        this.revealed = true;
    }
}
