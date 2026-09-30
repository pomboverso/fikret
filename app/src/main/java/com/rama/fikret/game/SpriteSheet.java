package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class SpriteSheet {
    private final Bitmap bitmap;
    private final int columns;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(Resources res, int resId, int columns, int rows) {
        this.bitmap = decode(res, resId);
        this.columns = columns;
        this.frameWidth = bitmap.getWidth() / columns;
        this.frameHeight = bitmap.getHeight() / rows;
    }

    private SpriteSheet(Resources res, int resId, int rows) {
        this.bitmap = decode(res, resId);
        this.frameHeight = bitmap.getHeight() / rows;
        this.columns = Math.max(1, Math.round(bitmap.getWidth() / (float) frameHeight));
        this.frameWidth = bitmap.getWidth() / columns;
    }

    public static SpriteSheet withSquareCells(Resources res, int resId, int rows) {
        return new SpriteSheet(res, resId, rows);
    }

    private static Bitmap decode(Resources res, int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
        if (bitmap == null) {
            throw new IllegalStateException("Could not decode drawable resource id " + resId);
        }
        return bitmap;
    }

    public int getColumns() {
        return columns;
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
