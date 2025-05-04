package com.tvz.avuckovic.the7thcitadel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExplorationArea {
    INTRO("Area intro"),
    FIRST("Area I"),
    SECOND("Area II"),
    THIRD("Area III"),
    FOURTH("Area IV"),
    FIFTH("Area V"),
    SIXTH("Area VI"),
    SEVENTH("Area VII"),
    EIGHT("Area VIII"),
    NINTH("Area IX"),
    TENTH("Area X"),
    NONE("Not an exploration area");

    private final String fullDescription;
}
