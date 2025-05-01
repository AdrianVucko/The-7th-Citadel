package com.example.the7thcitadel;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    public TextArea gameLog;

    @FXML
    public StackPane gameBoard;

    @FXML
    public ListView<?> inventoryList;
}