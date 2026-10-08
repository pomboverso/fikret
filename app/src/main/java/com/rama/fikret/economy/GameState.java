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
        int speedLevel;
        int valueLevel;
        double elapsed;     // seconds into the current cycle
        boolean running;    // only meaningful for gardens without a manager
    }

    private static final class World {
        final Garden[] gardens = new Garden[Worlds.GARDENS_PER_WORLD];
        boolean travelManager;
        boolean travelBirdRecruited;

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
    private double money = Worlds.START_MONEY;
    private long lastSync;
    private int currentWorld = 0;

    private GameState(Context context) {
        prefs = PrefsManager.getInstance(context);
        for (int i = 0; i < worlds.length; i++) {
            worlds[i] = new World();
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
            money += cycles * revenue;
            garden.elapsed -= cycles * cycle;
        } else {
            money += revenue;
            garden.elapsed = 0;
            garden.running = false;
        }
    }

    // ---- Read-only numbers --------------------------------------------------------------------

    public synchronized double getMoney() {
        return money;
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
        return Worlds.ALL[w].gardens[g].baseRevenue * garden.count * valueMultiplier(w, g);
    }

    public double revenuePerSecond(int w, int g) {
        return revenuePerCycle(w, g) / cycleSeconds(w, g);
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

    /** Cost of buying {@code amount} gardens (or as many as affordable for {@link #BUY_MAX}). */
    public synchronized Quote quote(int w, int g, int amount) {
        Worlds.GardenDef def = Worlds.ALL[w].gardens[g];
        int owned = worlds[w].gardens[g].count;
        double first = def.baseCost * Math.pow(def.coefficient, owned);
        double ratio = def.coefficient;

        if (amount == BUY_MAX) {
            double n = Math.floor(Math.log(money * (ratio - 1) / first + 1) / Math.log(ratio));
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
        Quote q = quote(w, g, amount);
        if (q.amount <= 0 || q.cost > money) {
            return false;
        }
        money -= q.cost;
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
        if (garden.manager || money < cost) {
            return false;
        }
        money -= cost;
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
        if (world.travelManager || money < cost) {
            return false;
        }
        money -= cost;
        world.travelManager = true;
        save();
        return true;
    }

    public boolean isTravelBirdRecruited(int w) {
        return worlds[w].travelBirdRecruited;
    }

    public synchronized void setTravelBirdRecruited(int w) {
        worlds[w].travelBirdRecruited = true;
        save();
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
        double factor = type == UPGRADE_SPEED ? Worlds.UPGRADE_SPEED_FACTOR : Worlds.UPGRADE_VALUE_FACTOR;
        return Worlds.ALL[w].gardens[g].baseCost * factor
                * Math.pow(Worlds.UPGRADE_GROWTH, upgradeLevel(w, g, type));
    }

    public synchronized boolean buyUpgrade(int w, int g, int type) {
        sync();
        if (upgradeMaxed(w, g, type)) {
            return false;
        }
        double cost = upgradeCost(w, g, type);
        if (money < cost) {
            return false;
        }
        money -= cost;
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
        sb.append("money=").append(Double.doubleToLongBits(money)).append('\n');
        sb.append("time=").append(lastSync).append('\n');
        sb.append("world=").append(currentWorld).append('\n');
        for (int w = 0; w < worlds.length; w++) {
            World world = worlds[w];
            sb.append(String.format(Locale.US, "w%d=%d,%d\n", w,
                    world.travelManager ? 1 : 0, world.travelBirdRecruited ? 1 : 0));
            for (int g = 0; g < world.gardens.length; g++) {
                Garden garden = world.gardens[g];
                sb.append(String.format(Locale.US, "g%d.%d=%d,%d,%d,%d,%d,%d\n", w, g,
                        garden.count, garden.manager ? 1 : 0, garden.speedLevel, garden.valueLevel,
                        garden.running ? 1 : 0, Double.doubleToLongBits(garden.elapsed)));
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
                    money = Double.longBitsToDouble(Long.parseLong(value));
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
                        worlds[w].travelBirdRecruited = p[1].equals("1");
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
                    }
                }
            }
        } catch (RuntimeException corrupt) {
            // A damaged save should not crash the app; fall back to what was read so far.
        }
    }
}
