package com.tvz.avuckovic.the7thcitadel.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameConstants {
    public static final class Board {
        public static final int ROWS = 20;
        public static final int COLS = 20;
        public static final int[] WATER_FIELDS = new int[]{
                0, 1, 20, 21, 20, 30, 40, 49, 50, 51, 60, 61, 62, 69, 70, 80, 81, 89, 100, 101, 120, 121, 122, 140, 141,
                159, 160, 161, 180, 181, 200, 219, 239, 258, 259, 260, 278, 279, 280, 298, 299, 300, 318, 319, 320, 321,
                338, 339, 340, 341, 342, 343, 351, 357, 358, 359, 360, 361, 362, 363, 377, 378, 379, 380, 381, 382, 383,
                384, 391, 395, 396, 397, 398, 399
        };
    }

    public static final class Player {
        public static final int CARDS_IN_HAND = 20;
        public static final int START_HEALTH = 8;
        public static final int MAX_HEALTH = 10;
    }

    public static final class UI {
        public static final String TITLE = "The 7th Citadel";
        public static final String STYLE_ROOT_FOLDER = "styles/";
        public static final String THEME = "app.css";
    }

    public static final class Page {
        public static final String ROOT_FOLDER = "fxml/";
        public static final String ROOT = "root-view.fxml";
        public static final String MAIN = "main-view.fxml";
        public static final String RULES = "rules-view.fxml";
        public static final String ABOUT = "about-view.fxml";
    }
}
