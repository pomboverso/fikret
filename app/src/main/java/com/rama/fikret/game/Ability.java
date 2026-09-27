package com.rama.fikret.game;

public enum Ability {
    SHOOT_MAGIC(Maps.BEACH),
    SEE_IN_DARK(Maps.FOREST_NEST),
    SWIM_NON_WATER(Maps.CAVE_NEST),
    HEALING(Maps.VOLCANO_NEST),
    DIVE_DEEP_WATER(Maps.NUCLEAR),
    THUNDER_ATTACK(Maps.ARCTIC_NEST),
    SONAR(Maps.BUBBLEGUM_LAND_NEST),
    RANDOM_TELEPORT(Maps.SPACE_NEST),
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
