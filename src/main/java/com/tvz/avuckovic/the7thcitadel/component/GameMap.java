package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.model.Message;
import com.tvz.avuckovic.the7thcitadel.utils.DialogUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GameMap extends GridPane {
    private static final int rows = GameConstants.Board.ROWS;
    private static final int cols = GameConstants.Board.COLS;
    private final Field[][] cells = new Field[rows][cols];
    private boolean isWaterWarningTriggered = false;
    private Field startField = null;
    private Pane progressDraw = null;
    private TextArea gameLog = null;

    public GameMap() {
        setHgap(0);
        setVgap(0);

        BigDecimal singleRowSpace = calculatePercentageSplit(rows);
        BigDecimal singleColumnSpace = calculatePercentageSplit(cols);
        for (int row = 0; row < rows; row++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(singleRowSpace.doubleValue());
            getRowConstraints().add(rowConstraint);
        }
        for (int col = 0; col < cols; col++) {
            ColumnConstraints colConstraint = new ColumnConstraints();
            colConstraint.setPercentWidth(singleColumnSpace.doubleValue());
            getColumnConstraints().add(colConstraint);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Field cell = new Field(row, col);
                add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    public void connectComponents(Pane progressDraw, TextArea gameLog) {
        this.progressDraw = progressDraw;
        this.gameLog = gameLog;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Field field = cells[row][col];
                field.setOnMouseClicked(e -> onMapFieldClick(field));
            }
        }
    }

    private void onMapFieldClick(Field field) {
        if (isClickPossible(field)) {
            if(startField != null) {
                drawPoint(startField);
                drawLine(startField, field);
            } else {
                drawPoint(field);
            }
            field.markAsRevealed();
            startField = field;
        }
    }

    private void drawPoint(Field startField) {
        Point2D start = getCenterInScene(startField);

        Circle redDot = new Circle();
        redDot.setCenterX(start.getX());
        redDot.setCenterY(start.getY());
        redDot.setRadius(5);
        redDot.setFill(Color.RED);

        this.progressDraw.getChildren().add(redDot);
    }

    private void drawLine(Field startField, Field endField) {
        Point2D start = getCenterInScene(startField);
        Point2D end = getCenterInScene(endField);

        Line dottedLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        dottedLine.setStrokeWidth(2);
        dottedLine.setStroke(Color.RED);

        this.progressDraw.getChildren().add(dottedLine);
    }

    private boolean isClickPossible(Field field) {
        boolean fieldInWater = field.isFieldInWater();
        if(fieldInWater && isWaterWarningTriggered) {
            DialogUtils.showDialog(Alert.AlertType.ERROR, Message.PLAYER_DROWNED.getText(),
                    Message.PLAYER_DROWNED.getText(),"");
            return false;
        }
        if(fieldInWater) {
            DialogUtils.showDialog(Alert.AlertType.WARNING, Message.PLAYER_WILL_DROWN_WARNING.getText(),
                    Message.PLAYER_WILL_DROWN_WARNING.getText(),"");
            isWaterWarningTriggered = true;
            return false;
        }

        if(field.isRevealed()) {
            gameLog.appendText("This place is already discovered!\n");
            return false;
        }
        return true;
    }

    private Point2D getCenterInScene(Node node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        Bounds relativeTo = progressDraw.sceneToLocal(bounds);
        double x = relativeTo.getMinX() + relativeTo.getWidth() / 2;
        double y = relativeTo.getMinY() + relativeTo.getHeight() / 2;
        return new Point2D(x, y);
    }

    private BigDecimal calculatePercentageSplit(int count) {
        BigDecimal divisor = new BigDecimal(count);
        return new BigDecimal("100.00").divide(divisor, 2, RoundingMode.UP);
    }
}
