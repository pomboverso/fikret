package com.rama.fikret.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

import com.rama.fikret.R;
import com.rama.fikret.economy.GameState;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.managers.PrefsManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private Stage stage;
    private int stageId;
    /** One idle bird per hired manager; they stay where they are and never chase. */
    private final List<Bird> managerBirds = new ArrayList<>();
    /** The bird of the "travel" manager: the only one that chases the player once touched. */
    private Bird travelBird;
    private final int[] travelChase = new int[2];
    private int worldIndex = -1;
    private int[] startPosition;
    private int holeGooseRow, holeGooseCol;
    private SpriteSheet tileSheet;
    private SpriteSheet itemSheet;
    private final Paint backgroundPaint = new Paint();

    private static final float SONAR_SPEED = 1.5f;
    private static final long SONAR_PING_MS = 600;
    private static final float SONAR_STROKE = 3f;
    private static final float SONAR_ECHO_GAP = 1f;
    private static final int SONAR_RINGS = 1;
    private static final long TELEPORT_PHASE_MS = 200;
    private static final long CAMERA_GLIDE_MIN_MS = 250;
    private static final long CAMERA_GLIDE_MAX_MS = 600;
    private static final float CAMERA_GLIDE_SPEED = 4f;   // world units per ms; longer jumps take longer, within the limits above
    private enum TeleportKind { HOME }
    private TeleportKind teleportKind;
    private boolean teleportArrived;
    private long teleportTimerMs;
    private static final long TAP_MAX_MS = 300;
    private static final float TAP_SLOP_DP = 14f;

    private final Paint sonarPaint = new Paint();
    private final List<SonarReveal> sonarPendingReveals = new ArrayList<>();
    private boolean sonarActive;
    private float sonarX;
    private float sonarY;
    private float sonarMaxRadius;
    private long sonarElapsedMs;
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();
    private final SwipeJoystick joystick;
    private final Blizzard blizzard;
    private int cameraX, cameraY;
    private boolean glidePending;
    private boolean cameraGliding;
    private int glideFromX, glideFromY;
    private long glideElapsedMs, glideDurationMs;
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 4f;
    private float zoomRaw = 2f;
    private float zoom = 2f;
    private PinchZoomDetector pinchZoomDetector;
    private float panOffsetX, panOffsetY;
    private float panAnchorX, panAnchorY;
    private boolean wasMoving;
    private static final long PAN_RESET_DURATION_MS = 200;
    private boolean panResetting;
    private long panResetElapsedMs;
    private float panResetStartX, panResetStartY;
    public interface OnExitListener {
        void onExitToFarm();
    }

    private OnExitListener exitListener;

    public void setOnExitListener(OnExitListener listener) {
        this.exitListener = listener;
    }

    private volatile boolean keyLeft, keyRight, keyUp, keyDown;
    private volatile int activeNumpadKeyCode = 0;
    private volatile int numpadDx, numpadDy;
    private volatile int stickDx, stickDy;
    private boolean pinching;
    private boolean tapCandidate;
    private float tapDownX, tapDownY;
    private float tapSlopPx;
    private long lastTapTime;
    private float lastTapX, lastTapY;
    private boolean longPressFired;
    private final Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            if (tapCandidate && !pinching) {
                longPressFired = true;
                tapCandidate = false;
                joystick.end();
                dive();
            }
        }
    };

    public GameView(Context context) {
        this(context, Maps.BEACH);
    }

    public GameView(Context context, int stageId) {
        this(context, stageId, null);
    }

    public GameView(Context context, int stageId, int[] startPosition) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        sonarPaint.setStyle(Paint.Style.STROKE);
        sonarPaint.setAntiAlias(true);
        sonarPaint.setColor(0xFF7FE8FF);
        float density = getResources().getDisplayMetrics().density;
        tapSlopPx = TAP_SLOP_DP * density;
        joystick = new SwipeJoystick(density);
        blizzard = new Blizzard(getResources());

        if (Build.VERSION.SDK_INT >= 8) {
            pinchZoomDetector = new PinchZoomDetector(context, new PinchZoomDetector.Listener() {
                @Override
                public void onZoom(float scaleFactor, float focusX, float focusY) {
                    setZoom(zoomRaw * scaleFactor);
                }
            });
        }

        this.startPosition = startPosition;
        try {
            stage = Maps.get(stageId);
        } catch (IllegalArgumentException unknownStage) {
            stageId = Maps.BEACH;
            stage = Maps.get(stageId);
            this.startPosition = null;
        }
        this.stageId = stageId;
        map = new GameMap(stage.tiles);
        worldIndex = Worlds.indexOfStage(stageId);
        GameState.get(context).onStageEntered(stageId);
        applyRevealedItems();
        loadTileSheets();
        loadItemSheet();
    }

    private void loadTileSheets() {
        if (tileSheet == null) {
            tileSheet = new SpriteSheet(getResources(), R.drawable.tiles, TileType.SHEET_COLUMNS, TileType.SHEET_ROWS);
        }
    }

    private void loadItemSheet() {
        if (itemSheet == null) {
            itemSheet = ItemSheet.get(getResources());
        }
    }

    private void setZoom(float newZoom) {
        zoomRaw = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));
        zoom = Math.round(zoomRaw);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (goose == null) {
            int row = stage.spawnRow;
            int col = stage.spawnCol;
            if (startPosition != null && map.isWalkable(startPosition[0], startPosition[1])) {
                row = startPosition[0];
                col = startPosition[1];
            }
            goose = new Goose(getResources(), row, col);
            placeBirds();
        }
        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
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
        clearInput();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pinchZoomDetector != null) {
            pinchZoomDetector.onTouchEvent(event);
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pinching = false;
                tapCandidate = true;
                longPressFired = false;
                tapDownX = event.getX();
                tapDownY = event.getY();
                joystick.begin(event.getX(), event.getY());
                postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout());
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                tapCandidate = false;
                removeCallbacks(longPressRunnable);
                pinching = true;
                panResetting = false;
                joystick.end();
                panAnchorX = averagePointerX(event);
                panAnchorY = averagePointerY(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    pinching = true;
                    joystick.end();
                    float avgX = averagePointerX(event);
                    float avgY = averagePointerY(event);
                    panOffsetX -= (avgX - panAnchorX) / zoom;
                    panOffsetY -= (avgY - panAnchorY) / zoom;
                    panAnchorX = avgX;
                    panAnchorY = avgY;
                } else if (!pinching) {
                    if (distance(event.getX(), event.getY(), tapDownX, tapDownY) > tapSlopPx) {
                        tapCandidate = false;
                        removeCallbacks(longPressRunnable);
                    }
                    joystick.move(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                return true;
            case MotionEvent.ACTION_UP:
                boolean isTap = tapCandidate
                        && event.getEventTime() - event.getDownTime() <= TAP_MAX_MS
                        && distance(event.getX(), event.getY(), tapDownX, tapDownY) <= tapSlopPx;
                tapCandidate = false;
                pinching = false;
                joystick.end();
                removeCallbacks(longPressRunnable);
                if (isTap && !longPressFired) {
                    boolean second = lastTapTime > 0
                            && event.getEventTime() - lastTapTime <= ViewConfiguration.getDoubleTapTimeout()
                            && distance(event.getX(), event.getY(), lastTapX, lastTapY) <= tapSlopPx * 4;
                    if (second) {
                        lastTapTime = 0;
                        sonar();
                    } else {
                        lastTapTime = event.getEventTime();
                        lastTapX = event.getX();
                        lastTapY = event.getY();
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                removeCallbacks(longPressRunnable);
                tapCandidate = false;
                pinching = false;
                joystick.end();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private static float averagePointerX(MotionEvent event) {
        float sum = 0f;
        int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            sum += event.getX(i);
        }
        return sum / count;
    }

    private static float averagePointerY(MotionEvent event) {
        float sum = 0f;
        int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            sum += event.getY(i);
        }
        return sum / count;
    }

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

    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return onKeyDown(event.getKeyCode(), event);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            return onKeyUp(event.getKeyCode(), event);
        }
        return false;
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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= 12 && GamepadAxes.isJoystickMove(event)) {
            stickDx = GamepadAxes.digitalX(event);
            stickDy = GamepadAxes.digitalY(event);
            return true;
        }
        return false;
    }

    private void clearInput() {
        keyLeft = false;
        keyRight = false;
        keyUp = false;
        keyDown = false;
        activeNumpadKeyCode = 0;
        numpadDx = 0;
        numpadDy = 0;
        stickDx = 0;
        stickDy = 0;
        pinching = false;
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
        wasMoving = false;
        joystick.end();
    }

    private int resolvedDx() {
        return sign(rawDx());
    }

    private int resolvedDy() {
        return sign(rawDy());
    }

    private int rawDx() {
        return (keyRight ? 1 : 0) - (keyLeft ? 1 : 0) + numpadDx + stickDx + joystick.getDx();
    }

    private int rawDy() {
        return (keyDown ? 1 : 0) - (keyUp ? 1 : 0) + numpadDy + stickDy + joystick.getDy();
    }

    private static int sign(int v) {
        return v > 0 ? 1 : (v < 0 ? -1 : 0);
    }

    public void update(long deltaMs) {
        if (goose == null) {
            return;
        }
        int dx = isTeleporting() ? 0 : resolvedDx();
        int dy = isTeleporting() ? 0 : resolvedDy();

        boolean nowMoving = dx != 0 || dy != 0;
        if (nowMoving && !wasMoving) {
            startPanReset();
        }
        wasMoving = nowMoving;

        goose.update(deltaMs, dx, dy, map);
        goose.setSwimming(isOverLiquid(goose.getX(), goose.getY()));
        checkHoleTransition();
        updateTeleport(deltaMs);
        updateSonar(deltaMs);
        updateTravelBird(deltaMs);
        updatePanReset(deltaMs);
        updateCamera(deltaMs);
        if (stage.hasBlizzard) {
            blizzard.update(deltaMs);
        }
    }

    private void startPanReset() {
        if (panOffsetX == 0f && panOffsetY == 0f) {
            return;
        }
        panResetting = true;
        panResetElapsedMs = 0;
        panResetStartX = panOffsetX;
        panResetStartY = panOffsetY;
    }

    private void updatePanReset(long deltaMs) {
        if (!panResetting) {
            return;
        }
        panResetElapsedMs += deltaMs;
        float t = Math.min(1f, panResetElapsedMs / (float) PAN_RESET_DURATION_MS);
        float eased = 1f - (1f - t) * (1f - t); // ease-out quad
        panOffsetX = panResetStartX * (1f - eased);
        panOffsetY = panResetStartY * (1f - eased);
        if (t >= 1f) {
            panResetting = false;
        }
    }

    private void checkHoleTransition() {
        if (goose.getRow() == holeGooseRow && goose.getCol() == holeGooseCol) {
            return;
        }
        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();

        MapCell cell = map.getCell(holeGooseRow, holeGooseCol);
        if (cell == null) {
            return;
        }
        if (cell.item == ItemType.HOLE_DOWN) {
            if (isHoleDownUnlocked()) {
                changeStage(stageId + 1);
            }
        } else if (cell.item == ItemType.HOLE_UP) {
            changeStage(isNestStage(stageId) ? stageId - Maps.NEST_OFFSET : stageId - 1);
        } else if (cell.item == ItemType.HOLE_DOWN_NEST) {
            if (Maps.hasNest(stageId)) {
                changeStage(stageId + Maps.NEST_OFFSET);
            }
        } else if (cell.item == ItemType.FARM_EXIT) {
            if (exitListener != null) {
                exitListener.onExitToFarm();
            }
        }
    }

    private static int diveTarget(ItemType item) {
        if (item == ItemType.DIVE_TO_ARCTIC) {
            return Maps.ARCTIC;
        } else if (item == ItemType.DIVE_TO_BEACH) {
            return Maps.BEACH;
        }
        return -1;
    }

    private static boolean isNestStage(int stageId) {
        return stageId >= Maps.NEST_OFFSET;
    }

    private void changeStage(int newStageId) {
        changeStage(newStageId, false);
    }

    private void changeStage(int newStageId, boolean resetWorld) {
        Stage newStage;
        try {
            newStage = Maps.get(newStageId);
        } catch (IllegalArgumentException noSuchStage) {
            return;
        }

        PrefsManager prefs = PrefsManager.getInstance(getContext());
        int leftStageId = stageId;
        int leftRow = goose.getRow();
        int leftCol = goose.getCol();

        stageId = newStageId;
        stage = newStage;
        map = new GameMap(stage.tiles);
        worldIndex = Worlds.indexOfStage(stageId);
        GameState.get(getContext()).onStageEntered(stageId);
        applyRevealedItems();
        resetSonar();
        cancelCameraGlide();

        int spawnRow = stage.spawnRow;
        int spawnCol = stage.spawnCol;
        if (!resetWorld) {
            int[] savedPos = prefs.getStagePosition(newStageId);
            if (savedPos != null && map.isWalkable(savedPos[0], savedPos[1])) {
                spawnRow = savedPos[0];
                spawnCol = savedPos[1];
            }
        }

        if (resetWorld) {
            prefs.saveTeleportHome(newStageId, spawnRow, spawnCol);
        } else {
            prefs.saveStageEntry(leftStageId, leftRow, leftCol, newStageId, spawnRow, spawnCol);
        }

        goose = new Goose(getResources(), spawnRow, spawnCol);
        placeBirds();

        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
        resetPan();
    }

    private void applyRevealedItems() {
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        for (int i = 0; i < stage.sonarReveals.size(); i++) {
            SonarReveal reveal = stage.sonarReveals.get(i);
            if (prefs.isSonarRevealed(stageId, reveal.row, reveal.col)) {
                map.addItem(reveal.row, reveal.col, reveal.item);
            }
        }
    }

    private void resetSonar() {
        sonarActive = false;
        sonarPendingReveals.clear();
    }

    private void resetPan() {
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
    }

    private void moveGooseTo(int row, int col) {
        goose.teleportTo(row, col);
        if (travelBird != null && travelBird.isFollowing()) {
            travelBird.teleportTo(row, col);
            travelChase[0] = row;
            travelChase[1] = col;
        }
        holeGooseRow = row;
        holeGooseCol = col;
        resetPan();
    }

    public boolean teleportHome() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.TELEPORT_HOME)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null || isTeleporting()) {
                return false;
            }
            startTeleport(TeleportKind.HOME);
            return true;
        }
    }

    private boolean isTeleporting() {
        return teleportKind != null;
    }

    private void startTeleport(TeleportKind kind) {
        teleportKind = kind;
        teleportArrived = false;
        teleportTimerMs = 0;
        goose.setTeleporting(true);
    }

    private void updateTeleport(long deltaMs) {
        if (!isTeleporting()) {
            return;
        }
        teleportTimerMs += deltaMs;
        if (!teleportArrived) {
            if (teleportTimerMs < TELEPORT_PHASE_MS) {
                return;
            }
            performTeleport();
            teleportArrived = true;
            teleportTimerMs = 0;
            goose.setTeleporting(true);
            return;
        }
        if (teleportTimerMs >= TELEPORT_PHASE_MS) {
            goose.setTeleporting(false);
            teleportKind = null;
        }
    }

    private void performTeleport() {
        if (stageId == Maps.BEACH) {
            beginCameraGlide();
            moveGooseTo(stage.spawnRow, stage.spawnCol);
        } else {
            changeStage(Maps.BEACH, true);
        }
    }

    public boolean dive() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.DIVE_DEEP_WATER)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null || isTeleporting()) {
                return false;
            }
            MapCell cell = map.getCell(goose.getRow(), goose.getCol());
            int target = cell == null ? -1 : diveTarget(cell.item);
            if (target < 0) {
                return false;
            }
            changeStage(target);
            return true;
        }
    }

    public boolean sonar() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.SONAR)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null || sonarActive) {
                return false;
            }
            float half = GameMap.TILE_SIZE / 2f;
            sonarX = goose.getX() + half;
            sonarY = goose.getY() + half;
            float farX = Math.max(sonarX, map.getWidthPx() - sonarX);
            float farY = Math.max(sonarY, map.getHeightPx() - sonarY);
            sonarMaxRadius = (float) Math.sqrt(farX * farX + farY * farY);

            sonarPendingReveals.clear();
            for (int i = 0; i < stage.sonarReveals.size(); i++) {
                SonarReveal reveal = stage.sonarReveals.get(i);
                MapCell cell = map.getCell(reveal.row, reveal.col);
                if (cell != null && !cell.hasItem() && reveal.zoneContains(goose.getRow(), goose.getCol())) {
                    sonarPendingReveals.add(reveal);
                }
            }
            sonarElapsedMs = 0;
            sonarActive = true;
            return true;
        }
    }

    private void updateSonar(long deltaMs) {
        if (!sonarActive) {
            return;
        }
        sonarElapsedMs += deltaMs;
        float radius = sonarElapsedMs * SONAR_SPEED;

        float half = GameMap.TILE_SIZE / 2f;
        for (Iterator<SonarReveal> it = sonarPendingReveals.iterator(); it.hasNext(); ) {
            SonarReveal reveal = it.next();
            float dx = reveal.col * GameMap.TILE_SIZE + half - sonarX;
            float dy = reveal.row * GameMap.TILE_SIZE + half - sonarY;
            if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                if (map.addItem(reveal.row, reveal.col, reveal.item)) {
                    PrefsManager.getInstance(getContext()).setSonarRevealed(stageId, reveal.row, reveal.col);
                }
                it.remove();
            }
        }

        if (sonarElapsedMs >= sonarMaxRadius / SONAR_SPEED + SONAR_PING_MS) {
            resetSonar();
        }
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private boolean isOverLiquid(float spriteX, float spriteY) {
        return map.isLiquidAt(spriteX + GameMap.TILE_SIZE / 2f, spriteY + GameMap.TILE_SIZE / 2f);
    }

    // ---- Birds ------------------------------------------------------------------------------

    /** The hole to the next world only exists once the world's travel manager has been hired. */
    private boolean isHoleDownUnlocked() {
        if (worldIndex < 0) {
            return true;   // caves, nests and other non-farm stages keep their doors
        }
        return GameState.get(getContext()).hasTravelManager(worldIndex);
    }

    /** Rebuilds every bird of the current stage from the hired managers. */
    private void placeBirds() {
        managerBirds.clear();
        travelBird = null;
        if (worldIndex < 0 || goose == null) {
            return;
        }
        GameState state = GameState.get(getContext());

        // Manager birds float around the liquid at random; with no liquid they use the whole map.
        List<int[]> spots = candidateSpots(true);
        if (spots.isEmpty()) {
            spots = candidateSpots(false);
        }
        Random random = new Random();
        for (int g = 0; g < Worlds.GARDENS_PER_WORLD && !spots.isEmpty(); g++) {
            if (!state.hasManager(worldIndex, g)) {
                continue;
            }
            int[] spot = spots.remove(random.nextInt(spots.size()));
            Bird bird = new Bird(getResources(), spot[0], spot[1]);
            bird.setSwimming(map.getCell(spot[0], spot[1]).isLiquid());
            managerBirds.add(bird);
        }

        // The guide bird shows up right away next to the goose and chases it from the start.
        if (state.hasTravelManager(worldIndex)) {
            List<int[]> used = new ArrayList<>();
            used.add(new int[]{goose.getRow(), goose.getCol()});
            int[] spot = findFreeSpotNear(goose.getRow(), goose.getCol(), used);
            if (spot == null) {
                spot = new int[]{goose.getRow(), goose.getCol()};
            }
            travelBird = new Bird(getResources(), spot[0], spot[1]);
            travelBird.startFollowing();
            travelChase[0] = goose.getRow();
            travelChase[1] = goose.getCol();
        }
    }

    /** Empty cells of the map (no item on them), optionally only the liquid ones. */
    private List<int[]> candidateSpots(boolean liquidOnly) {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                MapCell cell = map.getCell(r, c);
                if (cell.hasItem() || (liquidOnly && !cell.isLiquid())) {
                    continue;
                }
                if (r == goose.getRow() && c == goose.getCol()) {
                    continue;
                }
                result.add(new int[]{r, c});
            }
        }
        return result;
    }

    private int[] findItem(ItemType item) {
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                if (map.getCell(r, c).item == item) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    /** Closest empty dry tile to (row, col), scanning outwards in rings; deterministic. */
    private int[] findFreeSpotNear(int row, int col, List<int[]> used) {
        int maxRadius = Math.max(map.getRows(), map.getCols());
        for (int radius = 1; radius <= maxRadius; radius++) {
            for (int dr = -radius; dr <= radius; dr++) {
                for (int dc = -radius; dc <= radius; dc++) {
                    if (Math.max(Math.abs(dr), Math.abs(dc)) != radius) {
                        continue;
                    }
                    int r = row + dr;
                    int c = col + dc;
                    MapCell cell = map.getCell(r, c);
                    if (cell == null || cell.hasItem() || cell.isLiquid() || isUsed(used, r, c)) {
                        continue;
                    }
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    private static boolean isUsed(List<int[]> used, int r, int c) {
        for (int i = 0; i < used.size(); i++) {
            int[] u = used.get(i);
            if (u[0] == r && u[1] == c) {
                return true;
            }
        }
        return false;
    }

    private void updateTravelBird(long deltaMs) {
        if (travelBird == null) {
            return;
        }
        if (travelBird.isFollowing()) {
            if (goose.getRow() != travelChase[0] || goose.getCol() != travelChase[1]) {
                travelBird.moveTowards(travelChase[0], travelChase[1]);
            }
            travelChase[0] = goose.getRow();
            travelChase[1] = goose.getCol();
        }
        travelBird.update(deltaMs);
        travelBird.setSwimming(isOverLiquid(travelBird.getX(), travelBird.getY()));
    }

    /** Where the goose stands, so leaving to a menu and coming back keeps the position. */
    public int[] getGoosePosition() {
        Goose g = goose;
        return g == null ? null : new int[]{g.getRow(), g.getCol()};
    }

    private void updateCamera(long deltaMs) {
        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW == 0 || viewH == 0) {
            return;
        }
        float visibleW = viewW / zoom;
        float visibleH = viewH / zoom;
        cameraX = Math.round(goose.getX() + panOffsetX) + GameMap.TILE_SIZE / 2 - (int) (visibleW / 2f);
        cameraY = Math.round(goose.getY() + panOffsetY) + GameMap.TILE_SIZE / 2 - (int) (visibleH / 2f);

        int maxCamX = Math.max(0, (int) (map.getWidthPx() - visibleW));
        int maxCamY = Math.max(0, (int) (map.getHeightPx() - visibleH));
        cameraX = clamp(cameraX, 0, maxCamX);
        cameraY = clamp(cameraY, 0, maxCamY);

        applyCameraGlide(deltaMs);
    }

    private void applyCameraGlide(long deltaMs) {
        if (glidePending) {
            glidePending = false;
            float dx = cameraX - glideFromX;
            float dy = cameraY - glideFromY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < 1f) {
                return;
            }
            glideDurationMs = (long) Math.max(CAMERA_GLIDE_MIN_MS,
                    Math.min(CAMERA_GLIDE_MAX_MS, distance / CAMERA_GLIDE_SPEED));
            glideElapsedMs = 0;
            cameraGliding = true;
            cameraX = glideFromX;
            cameraY = glideFromY;
            return;
        }
        if (!cameraGliding) {
            return;
        }
        glideElapsedMs += deltaMs;
        float t = Math.min(1f, glideElapsedMs / (float) glideDurationMs);
        float eased = t * t * (3f - 2f * t); // smoothstep
        cameraX = glideFromX + Math.round((cameraX - glideFromX) * eased);
        cameraY = glideFromY + Math.round((cameraY - glideFromY) * eased);
        if (t >= 1f) {
            cameraGliding = false;
        }
    }

    private void beginCameraGlide() {
        glidePending = true;
        glideFromX = cameraX;
        glideFromY = cameraY;
    }

    private void cancelCameraGlide() {
        glidePending = false;
        cameraGliding = false;
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

        int tileSize = GameMap.TILE_SIZE;
        float visibleW = canvas.getWidth() / zoom;
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

        drawBirds(canvas, managerBirds, tileSize);
        if (travelBird != null) {
            reusableDst.set(Math.round(travelBird.getX()) - cameraX, Math.round(travelBird.getY()) - cameraY,
                    Math.round(travelBird.getX()) - cameraX + tileSize, Math.round(travelBird.getY()) - cameraY + tileSize);
            travelBird.draw(canvas, reusableDst);
        }
        drawSonar(canvas, visibleW, visibleH);

        if (goose != null) {
            int screenX = Math.round(goose.getX()) - cameraX;
            int screenY = Math.round(goose.getY()) - cameraY;
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }

        canvas.restore();

        if (stage.hasBlizzard) {
            blizzard.draw(canvas, canvas.getWidth(), canvas.getHeight());
        }
        joystick.draw(canvas);
    }

    private void drawSonar(Canvas canvas, float visibleW, float visibleH) {
        if (!sonarActive) {
            return;
        }
        float cx = sonarX - cameraX;
        float cy = sonarY - cameraY;
        float radius = sonarElapsedMs * SONAR_SPEED;

        if (radius <= sonarMaxRadius) {
            float fade = 1f - radius / sonarMaxRadius;
            for (int i = 0; i < SONAR_RINGS; i++) {
                float r = radius - i * SONAR_ECHO_GAP;
                if (r <= 0f) {
                    break;
                }
                float stroke = SONAR_STROKE / (1f + i * 0.5f);
                if (!ringOnScreen(cx, cy, r, stroke, visibleW, visibleH)) {
                    continue;
                }
                sonarPaint.setAlpha((int) (220f * (0.3f + 0.7f * fade) / (1f + i * 1.5f)));
                sonarPaint.setStrokeWidth(stroke);
                canvas.drawCircle(cx, cy, r, sonarPaint);
            }
        }
    }

    private static boolean ringOnScreen(float cx, float cy, float r, float stroke, float w, float h) {
        float nearX = cx < 0f ? -cx : (cx > w ? cx - w : 0f);
        float nearY = cy < 0f ? -cy : (cy > h ? cy - h : 0f);
        float farX = Math.max(Math.abs(cx), Math.abs(cx - w));
        float farY = Math.max(Math.abs(cy), Math.abs(cy - h));
        double near = Math.sqrt(nearX * nearX + nearY * nearY);
        double far = Math.sqrt(farX * farX + farY * farY);
        return r + stroke >= near && r - stroke <= far;
    }

    private void drawBirds(Canvas canvas, List<Bird> birds, int tileSize) {
        for (int i = 0; i < birds.size(); i++) {
            Bird bird = birds.get(i);
            int bx = Math.round(bird.getX()) - cameraX;
            int by = Math.round(bird.getY()) - cameraY;
            reusableDst.set(bx, by, bx + tileSize, by + tileSize);
            bird.draw(canvas, reusableDst);
        }
    }

    private void drawTile(Canvas canvas, int row, int col, int tileSize) {
        MapCell cell = map.getCell(row, col);
        if (cell == null) {
            return;
        }

        if (cell.hasBackground()) {
            drawTileLayer(canvas, cell.backgroundTile, cell.backgroundPosition, row, col, tileSize);
        }
        drawTileLayer(canvas, cell.tile, cell.position, row, col, tileSize);

        boolean hiddenHole = cell.item == ItemType.HOLE_DOWN && !isHoleDownUnlocked();
        if (cell.hasItem() && !hiddenHole && cell.item.hasSprite() && itemSheet != null) {
            int screenX = col * tileSize - cameraX;
            int screenY = row * tileSize - cameraY;
            reusableSrc.set(itemSheet.frameRect(cell.item.spriteCol, cell.item.spriteRow));
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            canvas.drawBitmap(itemSheet.getBitmap(), reusableSrc, reusableDst, null);
        }
    }

    private void drawTileLayer(Canvas canvas, TileType type, int position, int row, int col, int tileSize) {
        if (type == TileType.NONE) {
            return;
        }
        if (tileSheet == null) {
            return;
        }

        reusableSrc.set(tileSheet.frameRect(
                type.blockCol + TilePosition.col(position),
                type.blockRow + TilePosition.row(position)));
        int screenX = col * tileSize - cameraX;
        int screenY = row * tileSize - cameraY;
        reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
        canvas.drawBitmap(tileSheet.getBitmap(), reusableSrc, reusableDst, null);
    }
}
