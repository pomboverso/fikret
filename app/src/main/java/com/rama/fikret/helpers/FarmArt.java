package com.rama.fikret.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import com.rama.fikret.R;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.game.Maps;

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

    public static Drawable supervisor(Context context, int world) {
        return load(context, world, R.drawable.supervisor);
    }

    public static Drawable coin(Context context, int world, int sizePx) {
        Drawable coin = load(context, world, R.drawable.coin);
        coin.setBounds(0, 0, sizePx, sizePx);
        return coin;
    }
}
