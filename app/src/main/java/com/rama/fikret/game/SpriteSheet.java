package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * A bitmap cut into a fixed grid of equally-sized frames.
 *
 * Frame size is computed from the decoded bitmap's own dimensions divided
 * by the known column/row count, instead of being hardcoded in pixels, so
 * this doesn't care what resolution the source PNG actually is.
 *
 * gm_goose/gm_grass are plain PNGs in res/drawable-nodpi (NOT vector
 * drawables): the originals were vectors, but their pathData turned out to
 * be tens of thousands of characters long (auto-traced pixel art, not
 * hand-drawn vector shapes), which crashes or blanks out on a chunk of real
 * Android versions/OEM builds. drawable-nodpi tells Android to never
 * density-scale these, so every device decodes the exact same pixels.
 */
public class SpriteSheet {
    private final Bitmap bitmap;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(Resources res, int resId, int columns, int rows) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(res, resId, opts);
        if (this.bitmap == null) {
            throw new IllegalStateException("Could not decode drawable resource id " + resId);
        }
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


