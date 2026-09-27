package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.rama.fikret.R;

/**
 * A looping, full-screen blowing-snow overlay - see GameView.render(),
 * which draws it last, after canvas.restore() undoes the zoom/camera
 * transform, so the snow covers the viewport itself rather than the world
 * underneath it (it doesn't scale with zoom or scroll with the camera,
 * the same way the joystick drawn right after it doesn't either).
 *
 * R.drawable.blizzard is a single 768x768 tile of scattered snow
 * speckles, not a per-frame spritesheet like Bird/Goose - there's nothing
 * to flip between. The animation instead comes from continuously
 * scrolling that one tile diagonally (see update()) and repeating it
 * edge-to-edge to cover the screen (see draw()), the same trick a
 * side-scroller uses for a repeating background. Wrapping the scroll
 * offset at exactly the tile's own size means the pattern is simply back
 * where it started, mid-motion - so the loop point is never visible.
 *
 * Only actually driven for stages that want it - see Stage.hasBlizzard -
 * but always loaded up front alongside every other bit of stage art (see
 * GameView.loadTileSheets()/loadItemBitmaps()), rather than on demand, so
 * walking into an arctic-like stage never has to decode it first.
 */
public class Blizzard {
    // Pixels/second, screen space - Y > X so the snow reads as mostly
    // falling, with only a slight sideways drift from the wind.
    private static final float SPEED_X = 30f;
    private static final float SPEED_Y = 90f;

    private final Bitmap bitmap;
    private final int tileSize;
    private final Paint paint = new Paint();
    private float offsetX;
    private float offsetY;

    public Blizzard(Resources res) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        bitmap = BitmapFactory.decodeResource(res, R.drawable.blizzard, opts);
        tileSize = bitmap.getWidth(); // square asset - width == height
        paint.setFilterBitmap(false);
    }

    /** Advances the scroll offset, wrapping each axis back into [0,
     *  tileSize) - see class doc for why that wrap is what makes the loop
     *  seamless. */
    public void update(long deltaMs) {
        float seconds = deltaMs / 1000f;
        offsetX = wrap(offsetX + SPEED_X * seconds);
        offsetY = wrap(offsetY + SPEED_Y * seconds);
    }

    private float wrap(float value) {
        value %= tileSize;
        return value < 0 ? value + tileSize : value;
    }

    /** Tiles the snow texture across (0, 0)-(width, height) in screen
     *  space. Starts one whole tile above/left of the origin - offsetX/Y
     *  are always less than tileSize, so without that one-tile head start
     *  the current scroll position could leave a gap uncovered at the top
     *  or left edge. */
    public void draw(Canvas canvas, int width, int height) {
        int startX = (int) offsetX - tileSize;
        int startY = (int) offsetY - tileSize;
        for (int x = startX; x < width; x += tileSize) {
            for (int y = startY; y < height; y += tileSize) {
                canvas.drawBitmap(bitmap, x, y, paint);
            }
        }
    }
}
