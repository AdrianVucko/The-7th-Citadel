package com.tvz.avuckovic.the7thcitadel.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor
public class GameMove implements Serializable {
    private Player playerOne;
    private Player playerTwo;
    private List<Integer> completedFields;
}
