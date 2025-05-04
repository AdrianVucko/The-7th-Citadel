package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class Field extends StackPane {
    private final int row;
    private final int col;
    private boolean revealed;

    public Field(int row, int col) {
        this.row = row;
        this.col = col;
        this.revealed = false;

        setPrefSize(100, 100);
        setStyle("-fx-background-color: transparent;");
    }

    public boolean isFieldInWater() {
        int cellNumber = getRow() * GameConstants.Board.ROWS + getCol();
        return Arrays.stream(GameConstants.Board.WATER_FIELDS).anyMatch(waterField -> waterField == cellNumber);
    }

    public void markAsRevealed() {
        this.revealed = true;
    }
}
