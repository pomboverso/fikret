package com.rama.fikret.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.rama.fikret.game.Ability;

public class PrefsManager {
    private static final String PREFS_NAME = "fikret";
    private static final String KEY_ABILITY_UNLOCKED = "ability:unlocked";
    private static final String KEY_CURRENT_STAGE = "stage:current";
    private static final String KEY_ARRIVAL = "stage:arrival";
    private static final String KEY_STAGE_POSITION = "stage:position";
    private static final String KEY_SONAR_REVEALED = "sonar:revealed";
    private static PrefsManager instance;
    private final SharedPreferences prefs;

    private PrefsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsManager(context);
        }
        return instance;
    }

    private String key(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(':');
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    public boolean hasAbility(Ability ability) {
        return getBoolean(key(KEY_ABILITY_UNLOCKED, ability.name()), false);
    }

    public void unlockAbility(Ability ability) {
        setBoolean(key(KEY_ABILITY_UNLOCKED, ability.name()), true);
    }

    public void saveStageEntry(int leftStageId, int leftRow, int leftCol,
                               int newStageId, int arrivalRow, int arrivalCol) {
        prefs.edit()
                .putString(key(KEY_STAGE_POSITION, String.valueOf(leftStageId)), pair(leftRow, leftCol))
                .putInt(KEY_CURRENT_STAGE, newStageId)
                .putString(KEY_ARRIVAL, pair(arrivalRow, arrivalCol))
                .commit();
    }

    public void saveTeleportHome(int homeStageId, int arrivalRow, int arrivalCol) {
        SharedPreferences.Editor editor = prefs.edit();
        String positionPrefix = KEY_STAGE_POSITION + ":";
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(positionPrefix)) {
                editor.remove(key);
            }
        }
        editor.putInt(KEY_CURRENT_STAGE, homeStageId)
                .putString(KEY_ARRIVAL, pair(arrivalRow, arrivalCol))
                .commit();
    }

    /** Remembers where the player stands so coming back from a menu puts them in the same spot. */
    public void setArrivalPosition(int row, int col) {
        prefs.edit().putString(KEY_ARRIVAL, pair(row, col)).commit();
    }

    public int getCurrentStage(int defaultStageId) {
        return prefs.getInt(KEY_CURRENT_STAGE, defaultStageId);
    }

    public int[] getArrivalPosition() {
        return parsePair(prefs.getString(KEY_ARRIVAL, null));
    }

    public int[] getStagePosition(int stageId) {
        return parsePair(prefs.getString(key(KEY_STAGE_POSITION, String.valueOf(stageId)), null));
    }

    public boolean isSonarRevealed(int stageId, int row, int col) {
        return prefs.getBoolean(sonarKey(stageId, row, col), false);
    }

    public void setSonarRevealed(int stageId, int row, int col) {
        prefs.edit().putBoolean(sonarKey(stageId, row, col), true).commit();
    }

    private static String sonarKey(int stageId, int row, int col) {
        return KEY_SONAR_REVEALED + ":" + stageId + ":" + row + "," + col;
    }

    private static String pair(int row, int col) {
        return row + "," + col;
    }

    private static int[] parsePair(String value) {
        if (value == null) return null;
        String[] parts = value.split(",");
        if (parts.length != 2) return null;
        try {
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }
}
