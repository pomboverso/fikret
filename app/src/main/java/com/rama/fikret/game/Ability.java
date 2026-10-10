package com.rama.fikret.game;

public enum Ability {
    DIVE_DEEP_WATER(Maps.NUCLEAR),
    SONAR(Maps.BUBBLEGUM_LAND),
    TELEPORT_HOME(Maps.NIGHTMARE);

    public final int stageId;

    Ability(int stageId) {
        this.stageId = stageId;
    }

    public static Ability forStage(int stageId) {
        for (Ability ability : values()) {
            if (ability.stageId == stageId) {
                return ability;
            }
        }
        return null;
    }
}
