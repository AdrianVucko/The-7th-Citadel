package com.tvz.avuckovic.the7thcitadel.utils;

import com.tvz.avuckovic.the7thcitadel.component.Field;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DrawingUtils {
    public static void drawPoint(Pane progressDraw, Field startField) {
        Point2D start = getCenterInScene(progressDraw, startField);

        Circle redDot = new Circle();
        redDot.setCenterX(start.getX());
        redDot.setCenterY(start.getY());
        redDot.setRadius(5);
        redDot.setFill(Color.RED);

        progressDraw.getChildren().add(redDot);
    }

    public static void drawLine(Pane progressDraw, Field startField, Field endField) {
        Point2D start = getCenterInScene(progressDraw, startField);
        Point2D end = getCenterInScene(progressDraw, endField);

        Line dottedLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        dottedLine.setStrokeWidth(2);
        dottedLine.setStroke(Color.RED);

        progressDraw.getChildren().add(dottedLine);
    }

    private static Point2D getCenterInScene(Pane progressDraw, Node node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        Bounds relativeTo = progressDraw.sceneToLocal(bounds);
        double x = relativeTo.getMinX() + relativeTo.getWidth() / 2;
        double y = relativeTo.getMinY() + relativeTo.getHeight() / 2;
        return new Point2D(x, y);
    }
}
