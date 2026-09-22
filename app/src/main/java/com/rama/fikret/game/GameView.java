package com.rama.fikret.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.EnumMap;

/**
 * The game surface: owns the map, the goose, the camera/zoom, and all input.
 *
 * Movement steps the goose one tile at a time. Primary control is tapping
 * (or holding) one of the 4 tiles directly next to the goose - up/down/left/
 * right - which are drawn highlighted with a placeholder arrow each frame
 * (swap drawDirectionArrow() for real arrow art whenever you have it).
 * Keyboard (arrows/WASD) and the numpad (1-9, 5 = stop) still work too,
 * mainly handy for testing on an emulator without touch.
 *
 * Pinch-to-zoom is wired up (ScaleGestureDetector, isolated in
 * PinchZoomDetector so it never loads on devices below API 8).
 *
 * This is deliberately a starting point: one map, one character, a camera
 * that follows the goose and clamps to the map edges. Stages live in
 * {@link Maps} - add one there and pass its id to this view's constructor
 * (or via GameActivity.EXTRA_STAGE) to switch maps. This is also the place
 * to add more layers (items, other characters, UI) as you build them out.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private Stage stage;
    private final EnumMap<TileType, SpriteSheet> tileSheets = new EnumMap<TileType, SpriteSheet>(TileType.class);
    private final Paint backgroundPaint = new Paint();
    private final Paint arrowTileFillPaint = new Paint();
    private final Paint arrowTileFillPressedPaint = new Paint();
    private final Paint arrowGlyphPaint = new Paint();
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();
    private final Path reusablePath = new Path();

    private int cameraX, cameraY;

    // --- Zoom ----------------------------------------------------------
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 4f;
    private float zoom = 2f; // default a bit zoomed in - 64px tiles read as small otherwise
    private PinchZoomDetector pinchZoomDetector; // null below API 8

    // --- Input state -----------------------------------------------------
    // Keyboard: arrow keys / WASD, combined additively for diagonals.
    private boolean keyLeft, keyRight, keyUp, keyDown;
    // Keyboard: numpad 1-9 sets the vector directly (5 = stop).
    private int activeNumpadKeyCode = 0;
    private float numpadDx, numpadDy;
    // Touch: which on-screen direction tile is currently pressed (0 if none).
    private int touchDx, touchDy;

    public GameView(Context context) {
        this(context, Maps.MEADOW);
    }

    public GameView(Context context, int stageId) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        arrowTileFillPaint.setColor(Color.WHITE);
        arrowTileFillPaint.setAlpha(50);
        arrowTileFillPressedPaint.setColor(Color.WHITE);
        arrowTileFillPressedPaint.setAlpha(110);
        arrowGlyphPaint.setColor(Color.WHITE);
        arrowGlyphPaint.setAlpha(200);
        arrowGlyphPaint.setAntiAlias(true);

        if (Build.VERSION.SDK_INT >= 8) {
            pinchZoomDetector = new PinchZoomDetector(context, new PinchZoomDetector.Listener() {
                @Override
                public void onZoom(float scaleFactor, float focusX, float focusY) {
                    setZoom(zoom * scaleFactor);
                }
            });
        }

        stage = Maps.get(stageId);
        map = new GameMap(stage.tiles);
        loadTileSheets();
    }

    private void loadTileSheets() {
        for (TileType type : TileType.values()) {
            tileSheets.put(type, new SpriteSheet(getResources(), type.atlasRes, type.atlasColumns, type.atlasRows));
        }
    }

    private void setZoom(float newZoom) {
        zoom = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        goose = new Goose(getResources(), stage.spawnRow, stage.spawnCol);
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

    // --- Touch input: tap/hold the tile next to the goose ---------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pinchZoomDetector != null) {
            pinchZoomDetector.onTouchEvent(event);
        }

        // A second finger means this is a pinch, not a directional tap -
        // don't also interpret it as a move request.
        if (event.getPointerCount() > 1) {
            touchDx = 0;
            touchDy = 0;
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updatePressedArrow(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchDx = 0;
                touchDy = 0;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /** Converts a screen touch point to world pixels and checks it against
     *  the 4 tiles next to the goose, setting touchDx/touchDy if it landed
     *  on one of them. */
    private void updatePressedArrow(float screenX, float screenY) {
        if (goose == null) {
            return;
        }
        float worldX = screenX / zoom + cameraX;
        float worldY = screenY / zoom + cameraY;

        touchDx = 0;
        touchDy = 0;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int dx = DIRECTIONS[i][0];
            int dy = DIRECTIONS[i][1];
            int neighborRow = goose.getRow() + dy;
            int neighborCol = goose.getCol() + dx;
            if (!map.isInBounds(neighborRow, neighborCol)) {
                continue;
            }
            float left = neighborCol * GameMap.TILE_SIZE;
            float top = neighborRow * GameMap.TILE_SIZE;
            if (worldX >= left && worldX < left + GameMap.TILE_SIZE
                    && worldY >= top && worldY < top + GameMap.TILE_SIZE) {
                touchDx = dx;
                touchDy = dy;
                return;
            }
        }
    }

    private static final int[][] DIRECTIONS = {
            {0, -1}, // up
            {0, 1},  // down
            {-1, 0}, // left
            {1, 0},  // right
    };

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

    /** Combines every input source into raw axis values (keyboard/numpad
     *  can be diagonal), then collapses that down to a single cardinal
     *  direction - grid movement only ever steps in one axis at a time.
     *  Vertical wins when both axes are pressed. Touch is already cardinal
     *  only, since it comes from a single neighbor tile. */
    private int resolvedDx() {
        return resolvedDy() != 0 ? 0 : sign(rawDx());
    }

    private int resolvedDy() {
        return sign(rawDy());
    }

    private float rawDx() {
        return (keyRight ? 1 : 0) - (keyLeft ? 1 : 0) + numpadDx + touchDx;
    }

    private float rawDy() {
        return (keyDown ? 1 : 0) - (keyUp ? 1 : 0) + numpadDy + touchDy;
    }

    private static int sign(float v) {
        return v > 0 ? 1 : (v < 0 ? -1 : 0);
    }

    // --- Update / render -------------------------------------------------

    /** Called from GameThread, off the UI thread - keep this cheap and
     *  avoid touching Views directly. */
    public void update(long deltaMs) {
        if (goose == null) {
            return;
        }
        goose.update(deltaMs, resolvedDx(), resolvedDy(), map);
        updateCamera();
    }

    private void updateCamera() {
        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW == 0 || viewH == 0) {
            return;
        }
        float visibleW = viewW / zoom;
        float visibleH = viewH / zoom;

        cameraX = (int) (goose.getX() + GameMap.TILE_SIZE / 2f - visibleW / 2f);
        cameraY = (int) (goose.getY() + GameMap.TILE_SIZE / 2f - visibleH / 2f);

        int maxCamX = Math.max(0, (int) (map.getWidthPx() - visibleW));
        int maxCamY = Math.max(0, (int) (map.getHeightPx() - visibleH));
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

        canvas.save();
        canvas.scale(zoom, zoom);
        // Everything from here on draws in "world" pixels (pre-zoom) -
        // canvas.scale above already applies the zoom multiplication.

        int tileSize = GameMap.TILE_SIZE;
        float visibleW = canvas.getWidth() / zoom; // canvas dims are still physical px pre-scale-application to our math
        float visibleH = canvas.getHeight() / zoom;
        int firstCol = Math.max(0, cameraX / tileSize);
        int firstRow = Math.max(0, cameraY / tileSize);
        int lastCol = Math.min(map.getCols() - 1, (int) ((cameraX + visibleW) / tileSize) + 1);
        int lastRow = Math.min(map.getRows() - 1, (int) ((cameraY + visibleH) / tileSize) + 1);

        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = firstCol; c <= lastCol; c++) {
                drawTile(canvas, r, c, tileSize);
            }
        }

        drawDirectionTiles(canvas, tileSize);

        if (goose != null) {
            int screenX = (int) (goose.getX() - cameraX);
            int screenY = (int) (goose.getY() - cameraY);
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }

        canvas.restore();
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

    /** Highlights the (up to) 4 tiles next to the goose and draws a
     *  placeholder directional glyph on each - tap/hold one to move that
     *  way. Swap the glyph drawing for real arrow art whenever you have it
     *  (see the comment inside the loop for exactly where). */
    private void drawDirectionTiles(Canvas canvas, int tileSize) {
        if (goose == null) {
            return;
        }
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int dx = DIRECTIONS[i][0];
            int dy = DIRECTIONS[i][1];
            int neighborRow = goose.getRow() + dy;
            int neighborCol = goose.getCol() + dx;
            if (!map.isInBounds(neighborRow, neighborCol)) {
                continue;
            }

            int left = neighborCol * tileSize - cameraX;
            int top = neighborRow * tileSize - cameraY;
            reusableDst.set(left, top, left + tileSize, top + tileSize);

            boolean pressed = touchDx == dx && touchDy == dy;
            canvas.drawRect(reusableDst, pressed ? arrowTileFillPressedPaint : arrowTileFillPaint);

            // --- Placeholder glyph - replace with real arrow art later ---
            // e.g.: canvas.drawBitmap(arrowBitmapFor(dx, dy), null, reusableDst, null);
            drawArrowGlyph(canvas, reusableDst, dx, dy);
        }
    }

    private void drawArrowGlyph(Canvas canvas, Rect tile, int dx, int dy) {
        float pad = tile.width() * 0.28f;
        float cx = (tile.left + tile.right) / 2f;
        float cy = (tile.top + tile.bottom) / 2f;

        reusablePath.reset();
        if (dy < 0) { // up
            reusablePath.moveTo(cx, tile.top + pad);
            reusablePath.lineTo(tile.left + pad, tile.bottom - pad);
            reusablePath.lineTo(tile.right - pad, tile.bottom - pad);
        } else if (dy > 0) { // down
            reusablePath.moveTo(cx, tile.bottom - pad);
            reusablePath.lineTo(tile.left + pad, tile.top + pad);
            reusablePath.lineTo(tile.right - pad, tile.top + pad);
        } else if (dx < 0) { // left
            reusablePath.moveTo(tile.left + pad, cy);
            reusablePath.lineTo(tile.right - pad, tile.top + pad);
            reusablePath.lineTo(tile.right - pad, tile.bottom - pad);
        } else { // right
            reusablePath.moveTo(tile.right - pad, cy);
            reusablePath.lineTo(tile.left + pad, tile.top + pad);
            reusablePath.lineTo(tile.left + pad, tile.bottom - pad);
        }
        reusablePath.close();
        canvas.drawPath(reusablePath, arrowGlyphPaint);
    }
}
