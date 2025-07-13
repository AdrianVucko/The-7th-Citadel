package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.ExplorationArea;
import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import com.tvz.avuckovic.the7thcitadel.model.Message;
import com.tvz.avuckovic.the7thcitadel.model.Player;
import com.tvz.avuckovic.the7thcitadel.utils.DialogUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class GameMap extends GridPane {
    private static final int ROWS = GameConstants.Board.ROWS;
    private static final int COLS = GameConstants.Board.COLS;
    private final Field[][] cells = new Field[ROWS][COLS];
    private boolean isWaterWarningTriggered = false;
    private Field startField = null;
    private Pane progressDraw = null;

    public GameMap() {
        setHgap(0);
        setVgap(0);

        BigDecimal singleRowSpace = calculatePercentageSplit(ROWS);
        BigDecimal singleColumnSpace = calculatePercentageSplit(COLS);
        for (int row = 0; row < ROWS; row++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(singleRowSpace.doubleValue());
            getRowConstraints().add(rowConstraint);
        }
        for (int col = 0; col < COLS; col++) {
            ColumnConstraints colConstraint = new ColumnConstraints();
            colConstraint.setPercentWidth(singleColumnSpace.doubleValue());
            getColumnConstraints().add(colConstraint);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Field cell = new Field(row, col);
                add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    public void connectComponents(Pane progressDraw) {
        this.progressDraw = progressDraw;
        executeForEachField(field -> field.setOnMouseClicked(e -> onMapFieldClick(field)));
    }

    public boolean distributeActions(Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea, GameAction winningAction) {
        AtomicBoolean actionsDistributed = new AtomicBoolean(true);
        executeForEachField(field -> {
            List<GameAction> actions = actionsPerExplorationArea.get(field.getExplorationArea());
            GameAction gameAction = actions.get(field.getCellNumber() % actions.size());
            if(gameAction.equals(winningAction) && field.isFieldInWater()) {
                actionsDistributed.set(false);
            } else if (gameAction.equals(winningAction)) {
                field.markAsWinning();
                assignWinningExplorationAreaToFields(field.getExplorationArea());
            }
            field.assignAction(gameAction);
        });
        return actionsDistributed.get();
    }

    private void assignWinningExplorationAreaToFields(ExplorationArea winningExplorationArea) {
        executeForEachField(field -> field.setWinningExplorationArea(winningExplorationArea));
    }

    private void onMapFieldClick(Field field) {
        if (isClickPossible(field)) {
            boolean actionCompleted = field.triggerAction();
            if(field.isWinning() && !actionCompleted) {
                GameLogger.warn("You have found the final task, but failed it! Try again");
                field.highlight();
            } else {
                revealField(field, actionCompleted);
            }
        }
    }

    private void revealField(Field field, boolean actionCompleted) {
        if(startField != null) {
            drawPoint(startField);
            drawLine(startField, field);
        } else {
            drawPoint(field);
        }
        startField = field;
        field.markAsRevealed();
        if (field.isWinning() && actionCompleted) {
            throw new ApplicationException(Message.GAME_WON.getText(), Alert.AlertType.INFORMATION);
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
        if(isGameWon()) {
            throw new ApplicationException(Message.GAME_WON.getText(), Alert.AlertType.INFORMATION);
        }

        if(Player.getInstance().getHealth() == 0) {
            throw new ApplicationException(Message.ALREADY_DEAD.getText());
        }

        boolean fieldInWater = field.isFieldInWater();
        if(fieldInWater && isWaterWarningTriggered) {
            throw new ApplicationException(Message.PLAYER_DROWNED.getText());
        }
        if(fieldInWater) {
            DialogUtils.showDialog(Alert.AlertType.WARNING, Message.PLAYER_WILL_DROWN_WARNING.getText(),
                    Message.PLAYER_WILL_DROWN_WARNING.getText(),"");
            GameLogger.warn(Message.PLAYER_WILL_DROWN_WARNING.getText());
            isWaterWarningTriggered = true;
            return false;
        }

        if(field.isRevealed()) {
            GameLogger.info("This place is already discovered!");
            return false;
        }
        return true;
    }

    private boolean isGameWon() {
        AtomicBoolean gameWon = new AtomicBoolean(false);
        executeForEachField(field -> {
            if(field.isWinning() && field.isRevealed()) {
                gameWon.set(true);
            }
        });
        return gameWon.get();
    }

    public void executeForEachField(Consumer<Field> action) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                action.accept(cells[row][col]);
            }
        }
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