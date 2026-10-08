package com.rama.fikret.economy;

import com.rama.fikret.game.Maps;

/**
 * Static definition of every farm (world) and its ten gardens.
 *
 * All the numbers that still need balancing live in this file so they are easy to find:
 * manager prices, travel-manager prices, upgrade pricing and the whole Brazil price table.
 */
public final class Worlds {
    private Worlds() {
    }

    public static final String CURRENCY = "A$";
    public static final int GARDENS_PER_WORLD = 10;

    // ---- Placeholder balancing (not provided yet, tune freely) -------------------------------

    /** Money the player starts with, enough to buy the first garden. */
    public static final double START_MONEY = 4;

    /** Cost to hire the manager of garden i (same for every world until told otherwise). */
    private static final double[] PLACEHOLDER_MANAGER_COSTS = {
            1e3, 1.5e4, 1e5, 5e5, 1.2e6, 1e7, 1.11e8, 5.55e8, 1e10, 1e11
    };

    /** Money needed to unlock manager #11 (the one that opens the way to the next world). */
    private static final double PLACEHOLDER_TRAVEL_COST = 1e12;

    /** Upgrade price = garden base cost * FACTOR * GROWTH^level. */
    public static final double UPGRADE_VALUE_FACTOR = 100;
    public static final double UPGRADE_SPEED_FACTOR = 250;
    public static final double UPGRADE_GROWTH = 10;
    public static final int UPGRADE_MAX_LEVEL = 10;

    // ------------------------------------------------------------------------------------------

    public static final class GardenDef {
        public final String name;
        public final double baseCost;
        public final double coefficient;
        public final double cycleSeconds;
        public final double baseRevenue;

        GardenDef(String name, double baseCost, double coefficient, double cycleSeconds, double baseRevenue) {
            this.name = name;
            this.baseCost = baseCost;
            this.coefficient = coefficient;
            this.cycleSeconds = cycleSeconds;
            this.baseRevenue = baseRevenue;
        }
    }

    public static final class WorldDef {
        public final int stageId;
        public final String name;
        public final String country;
        public final GardenDef[] gardens;
        public final double[] managerCosts;
        public final double travelManagerCost;
        public final boolean placeholderPrices;

        WorldDef(int stageId, String name, String country, GardenDef[] gardens,
                 double[] managerCosts, double travelManagerCost, boolean placeholderPrices) {
            this.stageId = stageId;
            this.name = name;
            this.country = country;
            this.gardens = gardens;
            this.managerCosts = managerCosts;
            this.travelManagerCost = travelManagerCost;
            this.placeholderPrices = placeholderPrices;
        }
    }

    // value, coefficient, time (s), revenue per cycle for one unit
    private static final double[][] LAKE_NUMBERS = {
            {3.7, 1.07, 0.6, 1},
            {60, 1.15, 3, 60},
            {720, 1.14, 6, 540},
            {8640, 1.13, 12, 4320},
            {103680, 1.12, 24, 51840},
            {1244160, 1.11, 96, 622080},
            {14929920, 1.1, 384, 7464960},
            {179159040, 1.09, 1536, 89579520},
            {2149908480d, 1.08, 6144, 1074954240d},
            {25789901760d, 1.07, 36864, 29668737024d},
    };

    private static final String[] LAKE_NAMES = {
            "Yuca", "Mango", "Cajuil", "Guayaba", "Carambola",
            "Jobo", "Papaya", "Yautía", "Mamey", "Guanabana"
    };

    private static final String[] FOREST_NAMES = {
            "Abacaxi", "Maracujá", "Pitanga", "Jabuticaba", "Açaí",
            "Cupuaçu", "Guaraná", "Acerola", "Bacaba", "Tucumã"
    };

    private static GardenDef[] build(String[] names, double[][] numbers) {
        GardenDef[] result = new GardenDef[GARDENS_PER_WORLD];
        for (int i = 0; i < GARDENS_PER_WORLD; i++) {
            result[i] = new GardenDef(names[i], numbers[i][0], numbers[i][1], numbers[i][2], numbers[i][3]);
        }
        return result;
    }

    public static final WorldDef[] ALL = {
            new WorldDef(Maps.BEACH, "Lake", "Dominican Republic",
                    build(LAKE_NAMES, LAKE_NUMBERS),
                    PLACEHOLDER_MANAGER_COSTS, PLACEHOLDER_TRAVEL_COST, false),
            // Brazil has no prices yet: it reuses the lake numbers so the world is playable.
            new WorldDef(Maps.FOREST, "Forest", "Brazil",
                    build(FOREST_NAMES, LAKE_NUMBERS),
                    PLACEHOLDER_MANAGER_COSTS, PLACEHOLDER_TRAVEL_COST, true),
    };

    /** Index into {@link #ALL} for a stage id, or -1 when the stage is not a farm. */
    public static int indexOfStage(int stageId) {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].stageId == stageId) {
                return i;
            }
        }
        return -1;
    }

    public static String stageName(int stageId) {
        switch (stageId) {
            case Maps.BEACH:
                return "Lake (Dominican Republic)";
            case Maps.FOREST:
                return "Forest (Brazil)";
            case Maps.CAVE:
                return "Cave";
            case Maps.VOLCANO:
                return "Volcano";
            case Maps.NUCLEAR:
                return "Nuclear";
            case Maps.ARCTIC:
                return "Arctic";
            case Maps.BUBBLEGUM_LAND:
                return "Bubblegum land";
            case Maps.SPACE:
                return "Space";
            case Maps.NIGHTMARE:
                return "Nightmare";
            case Maps.BEACH_CAVE:
                return "Beach cave";
            case Maps.FOREST_NEST:
                return "Forest nest";
            case Maps.CAVE_NEST:
                return "Cave nest";
            case Maps.VOLCANO_NEST:
                return "Volcano nest";
            case Maps.ARCTIC_NEST:
                return "Arctic nest";
            case Maps.BUBBLEGUM_LAND_NEST:
                return "Bubblegum land nest";
            case Maps.SPACE_NEST:
                return "Space nest";
            default:
                return "Stage " + stageId;
        }
    }

    /** Every stage id that can be teleported to, in display order. */
    public static final int[] ALL_STAGE_IDS = {
            Maps.BEACH, Maps.FOREST, Maps.CAVE, Maps.VOLCANO, Maps.NUCLEAR, Maps.ARCTIC,
            Maps.BUBBLEGUM_LAND, Maps.SPACE, Maps.NIGHTMARE, Maps.BEACH_CAVE,
            Maps.FOREST_NEST, Maps.CAVE_NEST, Maps.VOLCANO_NEST,
            Maps.ARCTIC_NEST, Maps.BUBBLEGUM_LAND_NEST, Maps.SPACE_NEST
    };
}
