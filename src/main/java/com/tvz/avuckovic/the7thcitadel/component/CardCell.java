package com.tvz.avuckovic.the7thcitadel.component;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.controller.CardCellController;
import com.tvz.avuckovic.the7thcitadel.model.Card;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class CardCell extends ListCell<Card> {
    @Override
    protected void updateItem(Card card, boolean empty) {
        super.updateItem(card, empty);

        if (empty || card == null) {
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(RootController.class.getResource("fxml/card-cell.fxml"));
                AnchorPane pane = loader.load();
                CardCellController controller = loader.getController();
                controller.setCard(card);
                setGraphic(pane);
            } catch (Exception e) {
                e.printStackTrace();
                setGraphic(null);
            }
        }
    }
}
