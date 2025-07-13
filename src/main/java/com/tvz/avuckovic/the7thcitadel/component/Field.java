package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.model.ExplorationArea;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.utils.CardUtils;
import com.tvz.avuckovic.the7thcitadel.utils.DialogUtils;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class Field extends StackPane {
    private final int row;
    private final int col;
    private boolean revealed;
    private boolean winning;
    private ExplorationArea winningExplorationArea;
    private GameAction action;

    public Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.revealed = false;
        this.winning = false;

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

    public boolean triggerAction() {
        if(action != null) {
            return DialogUtils.showActionDialog(getWinningExplorationArea(), getExplorationArea(), action);
        }
        return false;
    }

    public void setWinningExplorationArea(ExplorationArea explorationArea) {
        this.winningExplorationArea = explorationArea;
    }

    public ExplorationArea getExplorationArea() {
        int areaNumber = evaluateAreaNumber();
        return CardUtils.evaluateExplorationAreaByNumber(areaNumber);
    }

    public int getCellNumber() {
        return getRow() * GameConstants.Board.ROWS + getCol();
    }

    public void markAsRevealed() {
        this.revealed = true;
    }

    public void markAsWinning() {
        this.winning = true;
        highlight();
    }

    public void highlight() {
        setStyle("-fx-border-color: yellow; -fx-background-color: rgba(255,255,0,0.3);");
    }

    private int evaluateAreaNumber() {
        return CardUtils.evaluateAreaNumber(getCellNumber());
    }
}
