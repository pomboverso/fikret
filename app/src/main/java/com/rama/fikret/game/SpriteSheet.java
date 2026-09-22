package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * A bitmap cut into a fixed grid of equally-sized frames.
 *
 * Frame size is computed from the decoded bitmap's own dimensions divided
 * by the known column/row count, instead of being hardcoded in pixels.
 * Vector drawables get rasterized into different-sized PNGs per screen
 * density at build time (since minSdk is below 21), so this keeps tile
 * slicing correct no matter which density bucket ends up loaded.
 */
public class SpriteSheet {
    private final Bitmap bitmap;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(Resources res, int resId, int columns, int rows) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(res, resId, opts);
        this.frameWidth = bitmap.getWidth() / columns;
        this.frameHeight = bitmap.getHeight() / rows;
    }

    public Rect frameRect(int col, int row) {
        int x = col * frameWidth;
        int y = row * frameHeight;
        return new Rect(x, y, x + frameWidth, y + frameHeight);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
