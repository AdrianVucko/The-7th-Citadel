package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.TheSeventhCitadelApplication;
import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import com.tvz.avuckovic.the7thcitadel.exception.ApplicationException;
import com.tvz.avuckovic.the7thcitadel.model.*;
import com.tvz.avuckovic.the7thcitadel.utils.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class GameMap extends GridPane {
    private static final int ROWS = GameConstants.Board.ROWS;
    private static final int COLS = GameConstants.Board.COLS;
    private final Field[][] cells = new Field[ROWS][COLS];
    private final List<Integer> completedFields;
    private boolean waterWarningTriggered = false;
    private boolean playerDrowned = false;
    private Field startField = null;
    private Pane progressDraw = null;
    private Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea;
    private GameAction winningAction;

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
        completedFields = new ArrayList<>();
    }

    public void connectComponents(Pane progressDraw) {
        this.progressDraw = progressDraw;
        executeForEachField(field -> field.setOnMouseClicked(e -> onMapFieldClick(field)));
    }

    public boolean distributeActions(Map<ExplorationArea, List<GameAction>> actionsPerExplorationArea,
                                     GameAction winningAction) {
        this.actionsPerExplorationArea = actionsPerExplorationArea;
        this.winningAction = winningAction;
        return GameActionUtils.distributeActionsOnMap(cells, actionsPerExplorationArea, winningAction);
    }

    public List<Integer> getCompletedFields() {
        return completedFields;
    }

    public void markCompletedFields(List<Integer> completedFields) {
        this.completedFields.clear();
        this.completedFields.addAll(completedFields);
        for (Integer completedField : this.completedFields) {
            Arrays.stream(cells)
                    .flatMap(Arrays::stream)
                    .filter(field -> field.getCellNumber() == completedField)
                    .findFirst()
                    .ifPresent(this::drawDirection);
        }
    }

    public void saveGameMove() {
        GameMove gameMove = buildGameMove();
        GameMoveUtils.saveGameMove(gameMove);
    }

    private void onMapFieldClick(Field field) {
        if (isClickPossible(field)) {
            boolean actionCompleted = field.triggerAction();
            if(field.isWinning() && !actionCompleted) {
                GameLogger.warn("You have found the final task, but failed it! Try again");
                field.highlight();
            } else {
                completeField(field, actionCompleted);
            }
        }
    }

    private void completeField(Field field, boolean actionCompleted) {
        drawDirection(field);
        completedFields.add(field.getCellNumber());
        saveGameMove();
        sendMoveToOtherPlayer();
        if (field.isWinning() && actionCompleted) {
            throw new ApplicationException(Message.GAME_WON.getText(), Alert.AlertType.INFORMATION);
        }
    }

    private void drawDirection(Field field) {
        if(startField != null) {
            DrawingUtils.drawPoint(progressDraw, startField);
            DrawingUtils.drawLine(progressDraw, startField, field);
        } else {
            DrawingUtils.drawPoint(progressDraw, field);
        }
        startField = field;
        field.markAsRevealed();
    }

    private boolean isClickPossible(Field field) {
        if(isGameWon()) {
            throw new ApplicationException(Message.GAME_WON.getText(), Alert.AlertType.INFORMATION);
        }
        if(Player.getInstance().getHealth() == 0) {
            throw new ApplicationException(Message.ALREADY_DEAD.getText());
        }
        checkMoveTurn();

        boolean fieldInWater = field.isFieldInWater();
        if(fieldInWater && waterWarningTriggered) {
            playerDrowned = true;
        } else if(fieldInWater) {
            DialogUtils.showDialog(Alert.AlertType.WARNING, Message.PLAYER_WILL_DROWN_WARNING.getText(),
                    Message.PLAYER_WILL_DROWN_WARNING.getText(),"");
            GameLogger.warn(Message.PLAYER_WILL_DROWN_WARNING.getText());
            waterWarningTriggered = true;
            return false;
        }
        if (playerDrowned) {
            throw new ApplicationException(Message.PLAYER_DROWNED.getText());
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

    private void executeForEachField(Consumer<Field> action) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                action.accept(cells[row][col]);
            }
        }
    }

    private BigDecimal calculatePercentageSplit(int count) {
        BigDecimal divisor = new BigDecimal(count);
        return new BigDecimal("100.00").divide(divisor, 2, RoundingMode.UP);
    }

    public GameMove buildGameMove() {
        return GameStateUtils.buildGameMove(completedFields);
    }

    public void checkMoveTurn() {
        if(!Player.getInstance().isOnMove()) {
            throw new ApplicationException(Message.NOT_YOUR_MOVE.getText());
        } else if (!FileUtils.fileExists(XmlUtils.GAME_MOVES_XML_FILE_NAME) &&
                TheSeventhCitadelApplication.applicationConfiguration.getPlayerType().equals(PlayerType.PLAYER_TWO)) {
            throw new ApplicationException("Player One needs to make first move for game to start");
        }
    }

    private void sendMoveToOtherPlayer() {
        if (GamePlayThreadsUtils.isMultiplayer()) {
            GameMove gameMove = buildGameMove();
            GameState gameState = GameStateUtils.buildGameState(actionsPerExplorationArea, winningAction, gameMove);
            GamePlayThreadsUtils.sendMove(gameState, true);
        }
    }
}