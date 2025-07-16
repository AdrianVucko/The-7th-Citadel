package com.tvz.avuckovic.the7thcitadel.controller;

import com.tvz.avuckovic.the7thcitadel.model.GameAction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ActionDisplayController {

    @FXML public Label requiredPointsLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label skillLabel;
    @FXML private Label pointsLabel;
    @FXML private Label gainLabel;
    @FXML private Label lossLabel;

    public void setAction(GameAction action) {
        descriptionLabel.setText("Action: " + action.description());
        requiredPointsLabel.setText("Minimum required: " + action.pointsNeeded() + "⭐");
        skillLabel.setText("Skill: " + action.requiredSkill());
        pointsLabel.setText("Skill Cards Needed: " + action.skillsNeeded());
        gainLabel.setText("Gain on Success: +" + action.healthGain() + " HP");
        lossLabel.setText("Loss on Failure: -" + action.healthLoss() + " HP");
    }
}
