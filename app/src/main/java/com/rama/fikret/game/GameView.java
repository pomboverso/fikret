package com.rama.fikret.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.EnumMap;

/**
 * The game surface: owns the map, the goose, the camera, and all input.
 * <p>
 * Movement is an 8-direction hold, fed from two sources that both write into
 * the same dx/dy state:
 * - Screen touch, split into a 3x3 grid of regions (same numpad layout used
 * everywhere else in this project: top-right region = up-right, center
 * region = stand still, etc).
 * - Keyboard: arrow keys / WASD (combine for diagonals), or the numeric
 * keypad 1-9 directly (5 = stop) for the same numpad-shaped input on a
 * physical/emulator keyboard.
 * <p>
 * This is deliberately a starting point: one map, one character, a camera
 * that follows the goose and clamps to the map edges. Swap out
 * {@link #buildTestMap()} for your own map data, and this is the place to
 * add more layers (items, other characters, UI) as you build them out.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private final EnumMap<TileType, SpriteSheet> tileSheets = new EnumMap<TileType, SpriteSheet>(TileType.class);
    private final Paint backgroundPaint = new Paint();
    private final Paint regionGridPaint = new Paint();
    private final Paint regionHighlightPaint = new Paint();
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();

    private int cameraX, cameraY;

    // --- Input state -------------------------------------------------
    // Keyboard: arrow keys / WASD, combined additively for diagonals.
    private boolean keyLeft, keyRight, keyUp, keyDown;
    // Keyboard: numpad 1-9 sets the vector directly (5 = stop), matches
    // the touch regions below key-for-key.
    private int activeNumpadKeyCode = 0;
    private float numpadDx, numpadDy;
    // Touch: which of the 3x3 screen regions is currently pressed, -1 if none.
    private int touchCol = -1, touchRow = -1;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        regionGridPaint.setColor(Color.WHITE);
        regionGridPaint.setAlpha(40);
        regionGridPaint.setStrokeWidth(2);
        regionHighlightPaint.setColor(Color.WHITE);
        regionHighlightPaint.setAlpha(35);

        map = new GameMap(buildTestMap());
        loadTileSheets();
    }

    /**
     * Small placeholder map: a grass rectangle using the border tiles
     * around the edge and the plain center tile (5) in the middle. Swap
     * this out for real map data whenever you're ready.
     */
    private int[][] buildTestMap() {
        return new int[][]{
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},

        };
    }

    private void loadTileSheets() {
        for (TileType type : TileType.values()) {
            tileSheets.put(type, new SpriteSheet(getResources(), type.atlasRes, type.atlasColumns, type.atlasRows));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        float startX = 2 * GameMap.TILE_SIZE;
        float startY = 2 * GameMap.TILE_SIZE;
        goose = new Goose(getResources(), startX, startY);
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Nothing to do yet: the camera reads getWidth()/getHeight() live each frame.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (thread == null) {
            return;
        }
        thread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

    // --- Touch input: 3x3 screen regions, numpad-style -----------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateTouchRegion(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchCol = -1;
                touchRow = -1;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void updateTouchRegion(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) {
            return;
        }
        touchCol = clampRegion((int) (x / (width / 3f)));
        touchRow = clampRegion((int) (y / (height / 3f)));
    }

    private static int clampRegion(int region) {
        return Math.max(0, Math.min(region, 2));
    }

    // --- Keyboard input: arrows/WASD + numpad 1-9 -----------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                keyLeft = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                keyRight = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                keyUp = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                keyDown = true;
                return true;
            default:
                if (keyCode >= KeyEvent.KEYCODE_NUMPAD_1 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                    int numpadPosition = keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    activeNumpadKeyCode = keyCode;
                    numpadDx = TilePosition.col(numpadPosition) - 1;
                    numpadDy = TilePosition.row(numpadPosition) - 1;
                    return true;
                }
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                keyLeft = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                keyRight = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                keyUp = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                keyDown = false;
                return true;
            default:
                if (keyCode == activeNumpadKeyCode) {
                    activeNumpadKeyCode = 0;
                    numpadDx = 0;
                    numpadDy = 0;
                    return true;
                }
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Combines every input source into one held direction, each axis
     * clamped to [-1, 1] (Goose normalizes the resulting vector).
     */
    private float inputDx() {
        float dx = (keyRight ? 1 : 0) - (keyLeft ? 1 : 0) + numpadDx;
        if (touchCol >= 0) {
            dx += touchCol - 1;
        }
        return Math.max(-1f, Math.min(1f, dx));
    }

    private float inputDy() {
        float dy = (keyDown ? 1 : 0) - (keyUp ? 1 : 0) + numpadDy;
        if (touchRow >= 0) {
            dy += touchRow - 1;
        }
        return Math.max(-1f, Math.min(1f, dy));
    }

    // --- Update / render -------------------------------------------------

    /**
     * Called from GameThread, off the UI thread - keep this cheap and
     * avoid touching Views directly.
     */
    public void update(long deltaMs) {
        if (goose == null) {
            return;
        }
        goose.update(deltaMs, inputDx(), inputDy(), map);
        updateCamera();
    }

    private void updateCamera() {
        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW == 0 || viewH == 0) {
            return;
        }

        cameraX = (int) (goose.getX() + GameMap.TILE_SIZE / 2f - viewW / 2f);
        cameraY = (int) (goose.getY() + GameMap.TILE_SIZE / 2f - viewH / 2f);

        int maxCamX = Math.max(0, map.getWidthPx() - viewW);
        int maxCamY = Math.max(0, map.getHeightPx() - viewH);
        cameraX = clamp(cameraX, 0, maxCamX);
        cameraY = clamp(cameraY, 0, maxCamY);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public void render(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        if (map == null) {
            return;
        }

        int tileSize = GameMap.TILE_SIZE;
        int firstCol = Math.max(0, cameraX / tileSize);
        int firstRow = Math.max(0, cameraY / tileSize);
        int lastCol = Math.min(map.getCols() - 1, (cameraX + canvas.getWidth()) / tileSize + 1);
        int lastRow = Math.min(map.getRows() - 1, (cameraY + canvas.getHeight()) / tileSize + 1);

        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = firstCol; c <= lastCol; c++) {
                drawTile(canvas, r, c, tileSize);
            }
        }

        if (goose != null) {
            int screenX = (int) (goose.getX() - cameraX);
            int screenY = (int) (goose.getY() - cameraY);
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }

        drawTouchRegions(canvas);
    }

    private void drawTile(Canvas canvas, int row, int col, int tileSize) {
        MapCell cell = map.getCell(row, col);
        if (cell == null) {
            return;
        }
        SpriteSheet sheet = tileSheets.get(cell.tileType);
        if (sheet == null) {
            return;
        }

        reusableSrc.set(sheet.frameRect(TilePosition.col(cell.position), TilePosition.row(cell.position)));
        int screenX = col * tileSize - cameraX;
        int screenY = row * tileSize - cameraY;
        reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
        canvas.drawBitmap(sheet.getBitmap(), reusableSrc, reusableDst, null);

        // Items (cell.itemId) get drawn here as their own layer once there's
        // item art - e.g. look up an ItemType by cell.itemId and draw its
        // sprite centered on the same reusableDst rect.
    }

    /**
     * Faint 3x3 grid + a highlight over whichever region is currently
     * pressed, so the touch controls are visible without needing button
     * art yet. Safe to delete once you have real on-screen controls.
     */
    private void drawTouchRegions(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float colWidth = width / 3f;
        float rowHeight = height / 3f;

        if (touchCol >= 0 && touchRow >= 0) {
            canvas.drawRect(touchCol * colWidth, touchRow * rowHeight,
                    (touchCol + 1) * colWidth, (touchRow + 1) * rowHeight, regionHighlightPaint);
        }

        canvas.drawLine(colWidth, 0, colWidth, height, regionGridPaint);
        canvas.drawLine(2 * colWidth, 0, 2 * colWidth, height, regionGridPaint);
        canvas.drawLine(0, rowHeight, width, rowHeight, regionGridPaint);
        canvas.drawLine(0, 2 * rowHeight, width, 2 * rowHeight, regionGridPaint);
    }
}
