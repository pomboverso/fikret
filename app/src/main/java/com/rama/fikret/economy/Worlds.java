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

    public static final int GARDENS_PER_WORLD = 10;

    /**
     * Every farm has its own money and its own economy: nothing earned in one farm can be spent in
     * another. Dominican peso for the lake, Brazilian real for the forest.
     */
    /** The farm whose earnings turn into angels when ascending (and whose profits angels boost). */
    public static final int ANGEL_WORLD = 0;
    /** Total angels earned = floor(sqrt(lifetime earnings / ANGEL_DIVISOR)); 4 billion / 9 * 100. */
    public static final double ANGEL_DIVISOR = 4e11 / 9;
    /** Each unspent angel adds +2% to the profits of the angel farm. */
    public static final double ANGEL_BONUS = 0.02;
    /** Angels ("lake points") are shown like a currency: LP10. */
    public static final String ANGEL_SYMBOL = "LP";
    public static final String CURRENCY_LAKE = "RD$";
    public static final String CURRENCY_FOREST = "R$";
    /** Indonesian rupiah, for the volcano farm once it gets its own WorldDef. */
    public static final String CURRENCY_VOLCANO = "Rp";

    // ---- Placeholder balancing (not provided yet, tune freely) -------------------------------

    /** Money the player starts with, enough to buy the first garden. */
    public static final double START_MONEY = 4;

    /** Cost to hire the manager of garden i (same for every world until told otherwise). */
    private static final double[] PLACEHOLDER_MANAGER_COSTS = {
            1e3, 1.5e4, 1e5, 5e5, 1.2e6, 1e7, 1.11e8, 5.55e8, 1e10, 1e11
    };

    /** Price of the guide bird (manager #11, the one that opens the way to the next world): 1 decillion. */
    private static final double GUIDE_BIRD_COST = 1e33;

    /**
     * Accountants (lake only) are bought with angels ("lake points"), which come from ascending.
     * Each one cuts the price of its garden to {@link #ACCOUNTANT_COST_FACTOR} of the normal price.
     * The list only had nine prices; the tenth (100 billion) continues the pattern and is a guess.
     */
    public static final double ACCOUNTANT_COST_FACTOR = 0.9;
    private static final double[] LAKE_ACCOUNTANT_COSTS = {
            10, 100, 1e3, 9999, 1e5, 1e7, 1e8, 1e9, 1e10, 1e11
    };

    /**
     * Discount managers (lake only) are bought with regular lake money, at a very high price. Each
     * one divides the price of its garden by {@link #DISCOUNT_COST_DIVISOR} (99.999% off).
     * Sequence: 10 tretrigintillion, 75 quattuortrigintillion, 250 quintrigintillion,
     * 100 sextrigintillion, 50 septentrigintillion, 3 octotrigintillion, 750 octotrigintillion,
     * 3 novemtrigintillion, 33 quadragintillion, 9 unquadragintillion.
     */
    public static final double DISCOUNT_COST_DIVISOR = 100000;
    private static final double[] LAKE_DISCOUNT_COSTS = {
            10e102, 75e105, 250e108, 100e111, 50e114, 3e117, 750e117, 3e120, 33e123, 9e126
    };

    /** Upgrade price before the stair-step adjustment = garden base cost * FACTOR * GROWTH^tier. */
    public static final double UPGRADE_VALUE_FACTOR = 100;
    public static final double UPGRADE_SPEED_FACTOR = 250;
    public static final double UPGRADE_GROWTH = 10;
    public static final int UPGRADE_MAX_LEVEL = 10;
    /** Every upgrade of the list costs at least this much more than the one listed right before it. */
    public static final double UPGRADE_MIN_STEP = 1.05;

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
        public final GardenDef[] gardens;
        public final String currency;
        public final double[] managerCosts;
        public final double travelManagerCost;
        /** Angel prices of the accountants, or null when the world has none. */
        public final double[] accountantCosts;
        /** Money prices of the discount managers, or null when the world has none. */
        public final double[] discountCosts;
        public final boolean placeholderPrices;

        WorldDef(int stageId, String name, String currency, GardenDef[] gardens,
                 double[] managerCosts, double travelManagerCost,
                 double[] accountantCosts, double[] discountCosts, boolean placeholderPrices) {
            this.stageId = stageId;
            this.name = name;
            this.currency = currency;
            this.gardens = gardens;
            this.managerCosts = managerCosts;
            this.travelManagerCost = travelManagerCost;
            this.accountantCosts = accountantCosts;
            this.discountCosts = discountCosts;
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
            new WorldDef(Maps.BEACH, "Lake", CURRENCY_LAKE,
                    build(LAKE_NAMES, LAKE_NUMBERS),
                    PLACEHOLDER_MANAGER_COSTS, GUIDE_BIRD_COST,
                    LAKE_ACCOUNTANT_COSTS, LAKE_DISCOUNT_COSTS, false),
            // Brazil has no prices yet: it reuses the lake numbers so the world is playable.
            new WorldDef(Maps.FOREST, "Forest", CURRENCY_FOREST,
                    build(FOREST_NAMES, LAKE_NUMBERS),
                    PLACEHOLDER_MANAGER_COSTS, GUIDE_BIRD_COST,
                    null, null, true),
    };

    // ---- Upgrade prices -----------------------------------------------------------------------

    /**
     * The upgrade list is a staircase: tier by tier, value upgrades first and then speed upgrades,
     * garden by garden. Each step must cost more than the one before it, so the natural price
     * (base cost * factor * 10^tier) is raised whenever it would be lower than the previous step.
     * Indexed [world][tier][speed ? 1 : 0][garden].
     */
    private static final double[][][][] UPGRADE_PRICES = buildUpgradePrices();

    private static double[][][][] buildUpgradePrices() {
        double[][][][] table = new double[ALL.length][UPGRADE_MAX_LEVEL][2][GARDENS_PER_WORLD];
        for (int w = 0; w < ALL.length; w++) {
            double previous = 0;
            for (int tier = 0; tier < UPGRADE_MAX_LEVEL; tier++) {
                for (int speed = 0; speed < 2; speed++) {   // value upgrades first, then speed
                    double factor = speed == 1 ? UPGRADE_SPEED_FACTOR : UPGRADE_VALUE_FACTOR;
                    for (int g = 0; g < GARDENS_PER_WORLD; g++) {
                        double natural = ALL[w].gardens[g].baseCost * factor * Math.pow(UPGRADE_GROWTH, tier);
                        double price = Math.max(natural, previous * UPGRADE_MIN_STEP);
                        table[w][tier][speed][g] = price;
                        previous = price;
                    }
                }
            }
        }
        return table;
    }

    /** Price of the upgrade of the given tier (0 = the first one) of a garden. */
    public static double upgradePrice(int world, int tier, boolean speed, int garden) {
        return UPGRADE_PRICES[world][tier][speed ? 1 : 0][garden];
    }

    public static String currencyOf(int world) {
        return ALL[world].currency;
    }

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
                return "Lake";
            case Maps.FOREST:
                return "Forest";
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
            case Maps.ARCTIC_NEST:
                return "Arctic secret farm";
            default:
                return "Stage " + stageId;
        }
    }

    /** Every stage id that can be teleported to, in display order. */
    public static final int[] ALL_STAGE_IDS = {
            Maps.BEACH, Maps.FOREST, Maps.CAVE, Maps.VOLCANO, Maps.NUCLEAR, Maps.ARCTIC,
            Maps.BUBBLEGUM_LAND, Maps.SPACE, Maps.NIGHTMARE, Maps.ARCTIC_NEST
    };
}
