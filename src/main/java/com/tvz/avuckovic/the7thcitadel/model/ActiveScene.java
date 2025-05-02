package com.tvz.avuckovic.the7thcitadel.model;

import lombok.Getter;

@Getter
public enum ActiveScene {
    MAIN("main-view.fxml"),
    RULES("rules-view.fxml"),
    ABOUT("about-view.fxml");

    private static final String ROOT_FOLDER = "fxml/";
    private final String filePath;

    ActiveScene(String fileName) {
        this.filePath = ROOT_FOLDER + fileName;
    }
}
