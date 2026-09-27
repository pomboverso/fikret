package com.rama.fikret.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.rama.fikret.game.Ability;

import java.util.ArrayList;
import java.util.List;

public class PrefsManager {
    private static final String PREFS_NAME = "fikret";
    private static final String KEY_BIRD_RESCUED = "bird:rescued";
    private static final String KEY_ABILITY_UNLOCKED = "ability:unlocked";
    private static final String KEY_HUD_EXPANDED = "hud:expanded";
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

    private List<String> splitCsv(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.length() == 0) return result;
        String[] parts = value.split(",");
        for (String part : parts) {
            if (part.length() > 0) result.add(part);
        }
        return result;
    }

    private String joinCsv(java.util.Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String value : values) {
            if (!first) sb.append(',');
            sb.append(value);
            first = false;
        }
        return sb.toString();
    }

    public boolean isBirdRescued(int stageId, int row, int col) {
        return getRescuedBirdKeys().contains(birdKey(stageId, row, col));
    }

    public void setBirdRescued(int stageId, int row, int col) {
        String key = birdKey(stageId, row, col);
        List<String> keys = getRescuedBirdKeys();
        if (!keys.contains(key)) {
            keys.add(key);
            prefs.edit().putString(KEY_BIRD_RESCUED, joinCsv(keys)).commit();
        }
    }

    public List<String> getRescuedBirdKeys() {
        return splitCsv(prefs.getString(KEY_BIRD_RESCUED, ""));
    }

    private String birdKey(int stageId, int row, int col) {
        return stageId + "_" + row + "_" + col;
    }

    public boolean hasAbility(Ability ability) {
        return getBoolean(key(KEY_ABILITY_UNLOCKED, ability.name()), false);
    }

    public void unlockAbility(Ability ability) {
        setBoolean(key(KEY_ABILITY_UNLOCKED, ability.name()), true);
    }

    public boolean isHudExpanded() {
        return getBoolean(KEY_HUD_EXPANDED, false);
    }

    public void setHudExpanded(boolean expanded) {
        setBoolean(KEY_HUD_EXPANDED, expanded);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }
}
