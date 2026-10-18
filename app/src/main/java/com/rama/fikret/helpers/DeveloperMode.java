package com.rama.fikret.helpers;

import com.rama.fikret.economy.Worlds;

/**
 * Global build switches, kept together so a release is configured in one place.
 */
public final class DeveloperMode {
    private DeveloperMode() {
    }

    /**
     * Testing switch. When true: every guide bird is active, the teleport menu is available and
     * every farm is reachable regardless of {@link #AVAILABLE_FARMS}. Must be false for release.
     */
    public static final boolean ENABLED = false;

    /**
     * How many farms are playable, in {@link Worlds#ALL} order (Lake, Forest, Cave, ...).
     * Raise this as each new farm is ready to ship. 1 = only the Lake/beach.
     */
    public static final int AVAILABLE_FARMS = 1;

    public static int availableFarms() {
        if (ENABLED) {
            return Worlds.ALL.length;
        }
        return Math.max(1, Math.min(AVAILABLE_FARMS, Worlds.ALL.length));
    }

    /** @param world index into {@link Worlds#ALL}; -1 (unknown stage) is not available. */
    public static boolean isFarmAvailable(int world) {
        return world >= 0 && world < availableFarms();
    }

    public static boolean isStageAvailable(int stageId) {
        return isFarmAvailable(Worlds.indexOfStage(stageId));
    }
}
