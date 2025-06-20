package com.tvz.avuckovic.the7thcitadel.model;

import com.tvz.avuckovic.the7thcitadel.constants.GameConstants;
import lombok.Getter;

@Getter
public enum ActiveScene {
    MAIN(GameConstants.Page.MAIN),
    RULES(GameConstants.Page.RULES),
    ABOUT(GameConstants.Page.ABOUT);

    private final String filePath;

    ActiveScene(String fileName) {
        this.filePath = GameConstants.Page.ROOT_FOLDER + fileName;
    }
}
