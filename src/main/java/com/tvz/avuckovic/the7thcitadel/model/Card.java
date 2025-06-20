package com.tvz.avuckovic.the7thcitadel.model;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Card implements Serializable {
    private String id;
    private CardType type;
    private String description;
    private SkillType skillType;
    private ExplorationArea explorationArea;
    private CardBackColor backColor;
    private CardFlag flag;
}
