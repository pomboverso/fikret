package com.rama.fikret.economy;

import android.content.Context;

import com.rama.fikret.game.Ability;
import com.rama.fikret.managers.PrefsManager;

import java.util.Locale;

/**
 * The whole idle economy. Time is wall-clock based: {@link #sync()} advances every garden by the
 * time elapsed since the last call, so the same code covers live play and offline progress.
 */
public final class GameState {
    public static final int BUY_MAX = -1;
    /** Buy exactly what is missing to reach the next milestone (25, 50, 100, 200, ...). */
    public static final int BUY_NEXT = -2;
    public static final int UPGRADE_SPEED = 0;
    public static final int UPGRADE_VALUE = 1;

    private static final String PREF_KEY = "economy:state";

    private static GameState instance;

    public static synchronized GameState get(Context context) {
        if (instance == null) {
            instance = new GameState(context.getApplicationContext());
        }
        return instance;
    }

    private static final class Garden {
        int count;
        boolean manager;
        boolean accountant;   // price x0.9 (bought with angels)
        boolean discount;     // price / 100,000
        int speedLevel;
        int valueLevel;
        double elapsed;     // seconds into the current cycle
        boolean running;    // only meaningful for gardens without a manager
    }

    private static final class World {
        final Garden[] gardens = new Garden[Worlds.GARDENS_PER_WORLD];
        boolean travelManager;

        World() {
            for (int i = 0; i < gardens.length; i++) {
                gardens[i] = new Garden();
            }
        }
    }

    /** Result of asking "what would buying N cost". */
    public static final class Quote {
        public final int amount;
        public final double cost;

        Quote(int amount, double cost) {
            this.amount = amount;
            this.cost = cost;
        }
    }

    private final PrefsManager prefs;
    private final World[] worlds = new World[Worlds.ALL.length];
    /** One purse per farm: each farm has its own currency, they never mix. */
    private final double[] money = new double[Worlds.ALL.length];
    /** Angels ("lake points"): the only thing that survives an ascent. Spent on accountants. */
    private double angels = 0;
    /** Everything ever earned in the angel farm. Never reset: it is what angels are computed from. */
    private double lifetimeEarnings = 0;
    /** Angels ever claimed by ascending, spent or not. Angels already claimed are not paid twice. */
    private double angelsClaimed = 0;
    private long lastSync;
    private int currentWorld = 0;

    private GameState(Context context) {
        prefs = PrefsManager.getInstance(context);
        for (int i = 0; i < worlds.length; i++) {
            worlds[i] = new World();
            money[i] = Worlds.START_MONEY;
        }
        load();
        if (lastSync <= 0) {
            lastSync = System.currentTimeMillis();
        }
        // Offline progress: load() restored the saved timestamp, so this credits the time away.
        sync();
    }

    // ---- Time ---------------------------------------------------------------------------------

    public synchronized void sync() {
        long now = System.currentTimeMillis();
        double dt = (now - lastSync) / 1000.0;
        lastSync = now;
        if (dt <= 0) {
            return;
        }
        for (int w = 0; w < worlds.length; w++) {
            for (int g = 0; g < Worlds.GARDENS_PER_WORLD; g++) {
                advance(w, g, dt);
            }
        }
    }

    private void advance(int w, int g, double dt) {
        Garden garden = worlds[w].gardens[g];
        if (garden.count <= 0) {
            return;
        }
        if (!garden.manager && !garden.running) {
            return;
        }
        double cycle = cycleSeconds(w, g);
        garden.elapsed += dt;
        if (garden.elapsed < cycle) {
            return;
        }
        double revenue = revenuePerCycle(w, g);
        if (garden.manager) {
            double cycles = Math.floor(garden.elapsed / cycle);
            earn(w, cycles * revenue);
            garden.elapsed -= cycles * cycle;
        } else {
            earn(w, revenue);
            garden.elapsed = 0;
            garden.running = false;
        }
    }

    private void earn(int w, double amount) {
        money[w] += amount;
        if (w == Worlds.ANGEL_WORLD) {
            lifetimeEarnings += amount;
        }
    }

    // ---- Read-only numbers --------------------------------------------------------------------

    /** Money of one farm, in that farm's own currency. */
    public synchronized double getMoney(int w) {
        return money[w];
    }

    public synchronized double getAngels() {
        return angels;
    }

    public int getCount(int w, int g) {
        return worlds[w].gardens[g].count;
    }

    public boolean isRunning(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        return garden.count > 0 && (garden.manager || garden.running);
    }

    public double progress(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        if (garden.count <= 0 || !(garden.manager || garden.running)) {
            return 0;
        }
        double cycle = cycleSeconds(w, g);
        // Gardens faster than a display frame would just flicker; show them full.
        if (cycle < 0.25) {
            return 1;
        }
        return Math.min(1, garden.elapsed / cycle);
    }

    public double timeLeft(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        if (garden.count <= 0 || !(garden.manager || garden.running)) {
            return 0;
        }
        return Math.max(0, cycleSeconds(w, g) - garden.elapsed);
    }

    public double cycleSeconds(int w, int g) {
        Worlds.GardenDef def = Worlds.ALL[w].gardens[g];
        return def.cycleSeconds / speedMultiplier(w, g);
    }

    public double speedMultiplier(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        return Math.pow(2, garden.speedLevel + milestonesReached(garden.count));
    }

    public double valueMultiplier(int w, int g) {
        return Math.pow(2, worlds[w].gardens[g].valueLevel);
    }

    public double revenuePerCycle(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        return Worlds.ALL[w].gardens[g].baseRevenue * garden.count * valueMultiplier(w, g)
                * angelBonus(w);
    }

    public double revenuePerSecond(int w, int g) {
        return revenuePerCycle(w, g) / cycleSeconds(w, g);
    }

    // ---- Angels -------------------------------------------------------------------------------

    /** Profit multiplier from the angels you hold: +2% each, only in the angel farm. */
    public synchronized double angelBonus(int w) {
        return w == Worlds.ANGEL_WORLD ? 1 + Worlds.ANGEL_BONUS * angels : 1;
    }

    /** Total angels a lifetime of earnings is worth, e.g. 150 billion is worth 1. */
    public static double angelsForEarnings(double lifetime) {
        return Math.floor(Math.sqrt(Math.max(0, lifetime) / Worlds.ANGEL_DIVISOR));
    }

    /** Angels you would receive right now by ascending (already claimed ones are not paid again). */
    public synchronized double pendingAngels() {
        return Math.max(0, angelsForEarnings(lifetimeEarnings) - angelsClaimed);
    }

    /**
     * Ascends: claims the pending angels and restarts the angel farm (money, gardens, supervisors,
     * discount managers, upgrades). Angels, accountants (bought with angels) and the guide bird stay.
     */
    public synchronized boolean ascend() {
        sync();
        double gain = pendingAngels();
        if (gain <= 0) {
            return false;
        }
        angels += gain;
        angelsClaimed += gain;
        int w = Worlds.ANGEL_WORLD;
        money[w] = Worlds.START_MONEY;
        for (Garden garden : worlds[w].gardens) {
            garden.count = 0;
            garden.manager = false;
            garden.discount = false;
            garden.speedLevel = 0;
            garden.valueLevel = 0;
            garden.elapsed = 0;
            garden.running = false;
        }
        save();
        return true;
    }

    // ---- Milestones (free x2 speed, like Adventure Capitalist) -------------------------------

    public static int milestonesReached(int count) {
        int m = 0;
        if (count >= 25) m++;
        if (count >= 50) m++;
        if (count >= 100) m++;
        if (count >= 200) m += count / 100 - 1;
        return m;
    }

    public static int nextMilestone(int count) {
        if (count < 25) return 25;
        if (count < 50) return 50;
        if (count < 100) return 100;
        return (count / 100 + 1) * 100;
    }

    // ---- Gardens ------------------------------------------------------------------------------

    /** The first garden is always open; the next ones open once the previous one is owned. */
    public boolean isUnlocked(int w, int g) {
        return g == 0 || worlds[w].gardens[g].count > 0 || worlds[w].gardens[g - 1].count > 0;
    }

    /** Accountant and discount manager of a garden make its price lower (they stack). */
    public double priceMultiplier(int w, int g) {
        Garden garden = worlds[w].gardens[g];
        double m = 1;
        if (garden.accountant) {
            m *= Worlds.ACCOUNTANT_COST_FACTOR;
        }
        if (garden.discount) {
            m /= Worlds.DISCOUNT_COST_DIVISOR;
        }
        return m;
    }

    /**
     * Cost of buying {@code amount} gardens. {@link #BUY_MAX} means as many as affordable and
     * {@link #BUY_NEXT} means as many as it takes to reach the next milestone, affordable or not.
     */
    public synchronized Quote quote(int w, int g, int amount) {
        Worlds.GardenDef def = Worlds.ALL[w].gardens[g];
        int owned = worlds[w].gardens[g].count;
        double first = def.baseCost * priceMultiplier(w, g) * Math.pow(def.coefficient, owned);
        double ratio = def.coefficient;

        if (amount == BUY_NEXT) {
            amount = nextMilestone(owned) - owned;
        } else if (amount == BUY_MAX) {
            double n = Math.floor(Math.log(money[w] * (ratio - 1) / first + 1) / Math.log(ratio));
            if (Double.isNaN(n) || n < 0) {
                n = 0;
            }
            amount = (int) Math.min(n, 100000);
        }
        if (amount <= 0) {
            return new Quote(0, first);
        }
        double cost = first * (Math.pow(ratio, amount) - 1) / (ratio - 1);
        return new Quote(amount, cost);
    }

    public synchronized boolean buy(int w, int g, int amount) {
        sync();
        if (!isUnlocked(w, g)) {
            return false;
        }
        Quote q = quote(w, g, amount);
        if (q.amount <= 0 || q.cost > money[w]) {
            return false;
        }
        money[w] -= q.cost;
        worlds[w].gardens[g].count += q.amount;
        save();
        return true;
    }

    /** Starts a cycle on a garden that has no manager. */
    public synchronized boolean harvest(int w, int g) {
        sync();
        Garden garden = worlds[w].gardens[g];
        if (garden.count <= 0 || garden.manager || garden.running) {
            return false;
        }
        garden.running = true;
        garden.elapsed = 0;
        return true;
    }

    // ---- Managers -----------------------------------------------------------------------------

    public boolean hasManager(int w, int g) {
        return worlds[w].gardens[g].manager;
    }

    public double managerCost(int w, int g) {
        return Worlds.ALL[w].managerCosts[g];
    }

    public synchronized boolean hireManager(int w, int g) {
        sync();
        Garden garden = worlds[w].gardens[g];
        double cost = managerCost(w, g);
        if (garden.manager || money[w] < cost) {
            return false;
        }
        money[w] -= cost;
        garden.manager = true;
        // A cycle that was started by hand simply keeps going; otherwise it starts from zero.
        if (!garden.running) {
            garden.elapsed = 0;
        }
        unlockAbilityFor(w);
        save();
        return true;
    }

    /** Hiring any manager of a world grants the ability tied to that world (if there is one). */
    private void unlockAbilityFor(int w) {
        Ability ability = Ability.forStage(Worlds.ALL[w].stageId);
        if (ability != null) {
            prefs.unlockAbility(ability);
        }
    }

    public boolean hasTravelManager(int w) {
        return worlds[w].travelManager;
    }

    public double travelManagerCost(int w) {
        return Worlds.ALL[w].travelManagerCost;
    }

    public synchronized boolean hireTravelManager(int w) {
        sync();
        World world = worlds[w];
        double cost = travelManagerCost(w);
        if (world.travelManager || money[w] < cost) {
            return false;
        }
        money[w] -= cost;
        world.travelManager = true;
        save();
        return true;
    }

    /** True when any farm has its guide bird: those birds follow the player into every farm. */
    public boolean hasAnyTravelManager() {
        for (World world : worlds) {
            if (world.travelManager) {
                return true;
            }
        }
        return false;
    }

    // ---- Accountants (angels) and discount managers (money) -----------------------------------

    public boolean hasAccountantList(int w) {
        return Worlds.ALL[w].accountantCosts != null;
    }

    public boolean hasDiscountList(int w) {
        return Worlds.ALL[w].discountCosts != null;
    }

    public boolean hasAccountant(int w, int g) {
        return worlds[w].gardens[g].accountant;
    }

    public boolean hasDiscount(int w, int g) {
        return worlds[w].gardens[g].discount;
    }

    /** Price of the accountant of garden g, in angels. */
    public double accountantCost(int w, int g) {
        return Worlds.ALL[w].accountantCosts[g];
    }

    /** Price of the discount manager of garden g, in the farm's money. */
    public double discountCost(int w, int g) {
        return Worlds.ALL[w].discountCosts[g];
    }

    public synchronized boolean hireAccountant(int w, int g) {
        sync();
        Garden garden = worlds[w].gardens[g];
        if (!hasAccountantList(w) || garden.accountant) {
            return false;
        }
        double cost = accountantCost(w, g);
        if (angels < cost) {
            return false;
        }
        angels -= cost;
        garden.accountant = true;
        save();
        return true;
    }

    public synchronized boolean hireDiscountManager(int w, int g) {
        sync();
        Garden garden = worlds[w].gardens[g];
        if (!hasDiscountList(w) || garden.discount) {
            return false;
        }
        double cost = discountCost(w, g);
        if (money[w] < cost) {
            return false;
        }
        money[w] -= cost;
        garden.discount = true;
        save();
        return true;
    }

    // ---- Upgrades -----------------------------------------------------------------------------

    public int upgradeLevel(int w, int g, int type) {
        Garden garden = worlds[w].gardens[g];
        return type == UPGRADE_SPEED ? garden.speedLevel : garden.valueLevel;
    }

    public boolean upgradeMaxed(int w, int g, int type) {
        return upgradeLevel(w, g, type) >= Worlds.UPGRADE_MAX_LEVEL;
    }

    public double upgradeCost(int w, int g, int type) {
        return upgradeCostAt(w, g, type, upgradeLevel(w, g, type));
    }

    /** Price of the upgrade of a given tier (0 = the first one) for a garden. */
    public double upgradeCostAt(int w, int g, int type, int tier) {
        return Worlds.upgradePrice(w, tier, type == UPGRADE_SPEED, g);
    }

    public synchronized boolean buyUpgrade(int w, int g, int type) {
        sync();
        if (upgradeMaxed(w, g, type)) {
            return false;
        }
        double cost = upgradeCost(w, g, type);
        if (money[w] < cost) {
            return false;
        }
        money[w] -= cost;
        Garden garden = worlds[w].gardens[g];
        if (type == UPGRADE_SPEED) {
            garden.speedLevel++;
        } else {
            garden.valueLevel++;
        }
        save();
        return true;
    }

    // ---- Which farm the player is in ----------------------------------------------------------

    public int getCurrentWorld() {
        return currentWorld;
    }

    /** Called whenever the map changes stage; non-farm stages (caves, nests) are ignored. */
    public synchronized void onStageEntered(int stageId) {
        int index = Worlds.indexOfStage(stageId);
        if (index >= 0 && index != currentWorld) {
            currentWorld = index;
            save();
        }
    }

    // ---- Persistence --------------------------------------------------------------------------

    public synchronized void save() {
        StringBuilder sb = new StringBuilder();
        for (int w = 0; w < money.length; w++) {
            // "money" is the lake purse (the only one old saves have), the others are "money.<world>".
            sb.append(w == 0 ? "money" : "money." + w).append('=')
                    .append(Double.doubleToLongBits(money[w])).append('\n');
        }
        sb.append("angels=").append(Double.doubleToLongBits(angels)).append('\n');
        sb.append("lifetime=").append(Double.doubleToLongBits(lifetimeEarnings)).append('\n');
        sb.append("claimed=").append(Double.doubleToLongBits(angelsClaimed)).append('\n');
        sb.append("time=").append(lastSync).append('\n');
        sb.append("world=").append(currentWorld).append('\n');
        for (int w = 0; w < worlds.length; w++) {
            World world = worlds[w];
            sb.append(String.format(Locale.US, "w%d=%d\n", w, world.travelManager ? 1 : 0));
            for (int g = 0; g < world.gardens.length; g++) {
                Garden garden = world.gardens[g];
                sb.append(String.format(Locale.US, "g%d.%d=%d,%d,%d,%d,%d,%d,%d,%d\n", w, g,
                        garden.count, garden.manager ? 1 : 0, garden.speedLevel, garden.valueLevel,
                        garden.running ? 1 : 0, Double.doubleToLongBits(garden.elapsed),
                        garden.accountant ? 1 : 0, garden.discount ? 1 : 0));
            }
        }
        prefs.putString(PREF_KEY, sb.toString());
    }

    private void load() {
        String data = prefs.getString(PREF_KEY, null);
        if (data == null) {
            return;
        }
        try {
            String[] lines = data.split("\n");
            for (String line : lines) {
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String key = line.substring(0, eq);
                String value = line.substring(eq + 1).trim();
                if (key.equals("money")) {
                    money[0] = Double.longBitsToDouble(Long.parseLong(value));
                } else if (key.startsWith("money.")) {
                    int w = Integer.parseInt(key.substring(6));
                    if (w >= 0 && w < money.length) {
                        money[w] = Double.longBitsToDouble(Long.parseLong(value));
                    }
                } else if (key.equals("angels")) {
                    angels = Double.longBitsToDouble(Long.parseLong(value));
                } else if (key.equals("lifetime")) {
                    lifetimeEarnings = Double.longBitsToDouble(Long.parseLong(value));
                } else if (key.equals("claimed")) {
                    angelsClaimed = Double.longBitsToDouble(Long.parseLong(value));
                } else if (key.equals("time")) {
                    lastSync = Long.parseLong(value);
                } else if (key.equals("world")) {
                    int w = Integer.parseInt(value);
                    if (w >= 0 && w < worlds.length) currentWorld = w;
                } else if (key.startsWith("w")) {
                    int w = Integer.parseInt(key.substring(1));
                    String[] p = value.split(",");
                    if (w < worlds.length) {
                        worlds[w].travelManager = p[0].equals("1");
                    }
                } else if (key.startsWith("g")) {
                    String[] ids = key.substring(1).split("\\.");
                    int w = Integer.parseInt(ids[0]);
                    int g = Integer.parseInt(ids[1]);
                    String[] p = value.split(",");
                    if (w < worlds.length && g < Worlds.GARDENS_PER_WORLD) {
                        Garden garden = worlds[w].gardens[g];
                        garden.count = Integer.parseInt(p[0]);
                        garden.manager = p[1].equals("1");
                        garden.speedLevel = Integer.parseInt(p[2]);
                        garden.valueLevel = Integer.parseInt(p[3]);
                        garden.running = p[4].equals("1");
                        garden.elapsed = Double.longBitsToDouble(Long.parseLong(p[5]));
                        garden.accountant = p.length > 6 && p[6].equals("1");
                        garden.discount = p.length > 7 && p[7].equals("1");
                    }
                }
            }
        } catch (RuntimeException corrupt) {
            // A damaged save should not crash the app; fall back to what was read so far.
        }
    }
}
