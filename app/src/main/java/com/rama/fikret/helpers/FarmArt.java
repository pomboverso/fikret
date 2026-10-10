package com.rama.fikret.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import com.rama.fikret.R;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.game.Maps;

/**
 * The coloured vectors of a farm (supervisor, coin). The vectors take their colours from theme
 * attributes, so the same drawable is tinted per farm by inflating it under that farm's style.
 * They must only be loaded through here: without a Farm_* style their colours do not resolve.
 */
public final class FarmArt {
    private FarmArt() {
    }

    private static int styleOf(int world) {
        switch (Worlds.ALL[world].stageId) {
            case Maps.FOREST:
                return R.style.Farm_Forest;
            case Maps.VOLCANO:
                return R.style.Farm_Volcano;
            default:
                return R.style.Farm_Lake;
        }
    }

    private static Drawable load(Context context, int world, int drawableRes) {
        return new ContextThemeWrapper(context, styleOf(world)).getDrawable(drawableRes);
    }

    /** The supervisor portrait in the colours of a farm. */
    public static Drawable supervisor(Context context, int world) {
        return load(context, world, R.drawable.supervisor);
    }

    /** The farm's coin as a square drawable of the given pixel size, ready for a compound drawable. */
    public static Drawable coin(Context context, int world, int sizePx) {
        Drawable coin = load(context, world, R.drawable.coin);
        coin.setBounds(0, 0, sizePx, sizePx);
        return coin;
    }
}
