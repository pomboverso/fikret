package com.rama.fikret.game;

/**
 * Permanent powers the goose gains, one at a time, by rescuing the single
 * bird that guards each of these stages - see GameView.checkBirdRescues(),
 * which calls forStage(stageId) the moment a bird is freed and, if it
 * returns non-null, unlocks that ability for the rest of the game (see
 * PrefsManager.unlockAbility()/hasAbility() - that's what actually makes
 * it stick across stages and app restarts, not this enum).
 *
 * Not every stage has a bird (e.g. the plain, non-nest stages other than
 * BEACH/NUCLEAR/NIGHTMARE), and not every bird has to grant an ability -
 * forStage() simply returns null for any stage id not listed below.
 */
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

    /** The stage whose bird grants this ability when rescued. */
    public final int stageId;

    Ability(int stageId) {
        this.stageId = stageId;
    }

    /** The ability granted by rescuing the bird on this stage, or null if
     *  that stage doesn't grant one (either it has no bird, or its bird is
     *  just a companion with no power attached). */
    public static Ability forStage(int stageId) {
        for (Ability ability : values()) {
            if (ability.stageId == stageId) {
                return ability;
            }
        }
        return null;
    }
}
