package com.tvz.avuckovic.the7thcitadel.model;

import java.io.Serializable;

public record GameAction(
        String description,
        SkillType requiredSkill,
        int skillsNeeded,
        int pointsNeeded,
        int healthGain,
        int healthLoss
) implements Serializable {
}
