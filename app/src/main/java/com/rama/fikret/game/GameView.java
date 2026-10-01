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

import com.rama.fikret.R;
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
    private final List<Bird> followingBirds = new ArrayList<>();
    private final List<int[]> chaseTargets = new ArrayList<>();
    private final List<Bird> idleBirds = new ArrayList<>();
    private final Random random = new Random();
    private int[] startPosition;
    private boolean canSwimNonWater;
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
    private enum TeleportKind { RANDOM, HOME }
    private TeleportKind teleportKind;
    private boolean teleportArrived;
    private long teleportTimerMs;
    private static final int ENEMY_SHEET_COLUMNS = 6;
    private static final int ENEMY_AGGRO_TILES = 7;
    private static final int ENEMY_MIN_SPAWN_DISTANCE = 8;
    private static final float TOUCH_DISTANCE = 40f;
    private static final long BULLET_INTERVAL_MS = 600;
    private static final int BULLET_RANGE_TILES = 6;
    private static final float BULLET_SPEED = 0.8f;
    private static final int BULLET_DAMAGE = 10;
    private static final float BULLET_HIT_DISTANCE = 22f;
    private static final long LIGHTNING_INTERVAL_MS = 2500;
    private static final int LIGHTNING_RANGE_TILES = 8;
    private static final int LIGHTNING_DAMAGE = 30;
    private static final long LIGHTNING_FLASH_MS = 180;
    private static final long TAP_MAX_MS = 300;
    private static final float TAP_SLOP_DP = 14f;
    private static final float TAP_TARGET_RADIUS = 56f;   // world units around an enemy's centre that count as tapping it
    private static final long EXPLOSION_MS = 350;
    private static final float EXPLOSION_RADIUS = 56f;
    private static final float[] LIGHTNING_JITTER = {0f, 14f, -12f, 10f, -8f, 6f, 0f};

    private static final class Explosion {
        final float x, y;
        long elapsedMs;

        Explosion(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public interface HpListener {
        void onHpChanged(int hp, int maxHp);
    }

    private final PlayerStats stats = new PlayerStats();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final Paint effectPaint = new Paint();
    private SpriteSheet enemySheet;
    private boolean enemySpawning = true;
    private boolean canShoot;
    private boolean hasThunder;
    private long bulletCooldownMs;
    private long lightningCooldownMs;
    private long lightningFlashMs;
    private float lightningX, lightningY;
    private int reportedHp = -1;
    private HpListener hpListener;
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
    private volatile boolean keyLeft, keyRight, keyUp, keyDown;
    private volatile int activeNumpadKeyCode = 0;
    private volatile int numpadDx, numpadDy;
    private volatile int stickDx, stickDy;
    private boolean pinching;
    private boolean tapCandidate;
    private float tapDownX, tapDownY;
    private float tapSlopPx;
    private OnAbilityUnlockedListener abilityUnlockedListener;

    public interface OnAbilityUnlockedListener {
        void onAbilityUnlocked(Ability ability);
    }

    public void setOnAbilityUnlockedListener(OnAbilityUnlockedListener listener) {
        this.abilityUnlockedListener = listener;
    }

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
        effectPaint.setAntiAlias(true);
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
        applyRevealedItems();
        refreshPassiveAbilities();
        loadTileSheets();
        loadItemSheet();
        findIdleBirdSpawns();
    }

    private void refreshPassiveAbilities() {
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        canSwimNonWater = prefs.hasAbility(Ability.SWIM_NON_WATER);
        canShoot = prefs.hasAbility(Ability.SHOOT_MAGIC);
        hasThunder = prefs.hasAbility(Ability.THUNDER_ATTACK);
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

    private void findIdleBirdSpawns() {
        idleBirds.clear();
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                if (map.getCell(r, c).item == ItemType.BIRD && !prefs.isBirdRescued(stageId, r, c)) {
                    idleBirds.add(new Bird(getResources(), r, c));
                }
            }
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
            if (startPosition != null && map.isWalkable(startPosition[0], startPosition[1], canSwimNonWater)) {
                row = startPosition[0];
                col = startPosition[1];
            }
            goose = new Goose(getResources(), row, col);
            restoreRescuedBirds();
            spawnEnemies();
        }
        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    private void restoreRescuedBirds() {
        List<String> rescuedKeys = PrefsManager.getInstance(getContext()).getRescuedBirdKeys();
        int row = goose.getRow();
        int col = goose.getCol();
        for (int i = 0; i < rescuedKeys.size(); i++) {
            Bird bird = new Bird(getResources(), row, col);
            bird.startFollowing();
            followingBirds.add(bird);
            chaseTargets.add(new int[]{row, col});
        }
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
                tapDownX = event.getX();
                tapDownY = event.getY();
                joystick.begin(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                tapCandidate = false;
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
                if (isTap) {
                    tapAt(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
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

        goose.update(deltaMs, dx, dy, map, canSwimNonWater);
        goose.setSwimming(isOverLiquid(goose.getX(), goose.getY()));
        checkHoleTransition();
        checkBirdRescues();
        updateTeleport(deltaMs);
        updateCombat(deltaMs);
        updateSonar(deltaMs);
        updateBirds(deltaMs);
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
            changeStage(stageId + 1);
        } else if (cell.item == ItemType.HOLE_UP) {
            changeStage(isNestStage(stageId) ? stageId - Maps.NEST_OFFSET : stageId - 1);
        } else if (cell.item == ItemType.HOLE_DOWN_NEST) {
            changeStage(stageId + Maps.NEST_OFFSET);
        }
    }

    private static int diveTarget(ItemType item) {
        if (item == ItemType.DIVE_TO_ARCTIC) {
            return Maps.ARCTIC;
        } else if (item == ItemType.DIVE_TO_BEACH_CAVE) {
            return Maps.BEACH_CAVE;
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
        applyRevealedItems();
        resetSonar();
        cancelCameraGlide();
        findIdleBirdSpawns();

        int spawnRow = stage.spawnRow;
        int spawnCol = stage.spawnCol;
        if (!resetWorld) {
            int[] savedPos = prefs.getStagePosition(newStageId);
            if (savedPos != null && map.isWalkable(savedPos[0], savedPos[1], canSwimNonWater)) {
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
        relocateFollowers(spawnRow, spawnCol);
        spawnEnemies();

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

    private void relocateFollowers(int row, int col) {
        for (int i = 0; i < followingBirds.size(); i++) {
            followingBirds.get(i).teleportTo(row, col);
            int[] chaseTarget = chaseTargets.get(i);
            chaseTarget[0] = row;
            chaseTarget[1] = col;
        }
    }

    private void resetPan() {
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
    }

    private void moveGooseTo(int row, int col) {
        goose.teleportTo(row, col);
        relocateFollowers(row, col);
        holeGooseRow = row;
        holeGooseCol = col;
        resetPan();
    }

    public boolean teleportRandom() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.RANDOM_TELEPORT)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null || isTeleporting()) {
                return false;
            }
            if (map.randomFreeCell(random, canSwimNonWater, goose.getRow(), goose.getCol()) == null) {
                return false;
            }
            startTeleport(TeleportKind.RANDOM);
            return true;
        }
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
        if (teleportKind == TeleportKind.RANDOM) {
            int[] spot = map.randomFreeCell(random, canSwimNonWater, goose.getRow(), goose.getCol());
            if (spot != null) {
                beginCameraGlide();
                moveGooseTo(spot[0], spot[1]);
            }
        } else if (stageId == Maps.BEACH) {
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

    public void setHpListener(HpListener listener) {
        synchronized (getHolder()) {
            hpListener = listener;
            reportedHp = -1;
            reportHp();
        }
    }

    public int getHp() {
        synchronized (getHolder()) {
            return stats.getHp();
        }
    }

    public void setEnemySpawning(boolean enabled) {
        synchronized (getHolder()) {
            enemySpawning = enabled;
            if (!enabled) {
                clearCombat();
            }
        }
    }

    private void clearCombat() {
        enemies.clear();
        bullets.clear();
        explosions.clear();
        lightningFlashMs = 0;
    }

    private void spawnEnemies() {
        clearCombat();
        if (!enemySpawning) {
            return;
        }
        EnemySpawns.Entry[] entries = EnemySpawns.forStage(stageId);
        if (entries.length == 0) {
            return;
        }
        if (enemySheet == null) {
            enemySheet = SpriteSheet.withSquareCellsByColumns(getResources(), R.drawable.enemies, ENEMY_SHEET_COLUMNS);
        }
        for (int i = 0; i < entries.length; i++) {
            for (int n = 0; n < entries[i].count; n++) {
                for (int attempt = 0; attempt < 10; attempt++) {
                    int[] spot = map.randomDryCellAway(random, goose.getRow(), goose.getCol(), ENEMY_MIN_SPAWN_DISTANCE);
                    if (spot == null) {
                        break;
                    }
                    if (!isEnemyAt(spot[0], spot[1])) {
                        enemies.add(new Enemy(entries[i].type, enemySheet, spot[0], spot[1]));
                        break;
                    }
                }
            }
        }
    }

    private boolean isEnemyAt(int row, int col) {
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).getRow() == row && enemies.get(i).getCol() == col) {
                return true;
            }
        }
        return false;
    }

    private void updateCombat(long deltaMs) {
        stats.update(deltaMs);
        goose.setStepMultiplier(stats.stepMultiplier());
        updateEnemies(deltaMs);
        updateAttackCooldowns(deltaMs);
        updateBullets(deltaMs);
        updateExplosions(deltaMs);
        if (stats.isDead()) {
            onPlayerDied();
        }
        reportHp();
    }

    private void updateEnemies(long deltaMs) {
        float half = GameMap.TILE_SIZE / 2f;
        float gx = goose.getX() + half;
        float gy = goose.getY() + half;
        float aggro = ENEMY_AGGRO_TILES * GameMap.TILE_SIZE;
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.isGone()) {
                enemies.remove(i);
                continue;
            }
            boolean chase = distance(enemy.getCenterX(), enemy.getCenterY(), gx, gy) <= aggro;
            enemy.update(deltaMs, map, goose.getRow(), goose.getCol(), chase, enemies);
            if (!isTeleporting() && enemy.canTouch()
                    && distance(enemy.getCenterX(), enemy.getCenterY(), gx, gy) < TOUCH_DISTANCE) {
                touch(enemy);
                if (enemy.type.explodes) {
                    explosions.add(new Explosion(enemy.getCenterX(), enemy.getCenterY()));
                    enemies.remove(i);
                }
            }
        }
    }

    private void touch(Enemy enemy) {
        EnemyType type = enemy.type;
        stats.damage(type.touchDamage);
        if (type.slowMs > 0) {
            stats.applySlow(type.slowMs);
        }
        if (type.poisonMs > 0) {
            stats.applyPoison(type.poisonMs, type.poisonDamage);
        }
        enemy.startTouchCooldown();
    }

    private void updateAttackCooldowns(long deltaMs) {
        bulletCooldownMs = Math.max(0, bulletCooldownMs - deltaMs);
        lightningCooldownMs = Math.max(0, lightningCooldownMs - deltaMs);
        if (lightningFlashMs > 0) {
            lightningFlashMs = Math.max(0, lightningFlashMs - deltaMs);
        }
    }

    public boolean tapAt(float screenX, float screenY) {
        synchronized (getHolder()) {
            if (goose == null || isTeleporting()) {
                return false;
            }
            Enemy target = enemyAt(screenX / zoom + cameraX, screenY / zoom + cameraY);
            return target != null && attack(target);
        }
    }

    private Enemy enemyAt(float worldX, float worldY) {
        float best = TAP_TARGET_RADIUS;
        Enemy tapped = null;
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.isDead()) {
                continue;
            }
            float d = distance(e.getCenterX(), e.getCenterY(), worldX, worldY);
            if (d <= best) {
                best = d;
                tapped = e;
            }
        }
        return tapped;
    }

    private boolean attack(Enemy target) {
        float half = GameMap.TILE_SIZE / 2f;
        float d = distance(target.getCenterX(), target.getCenterY(), goose.getX() + half, goose.getY() + half);
        boolean attacked = false;
        if (canShoot && bulletCooldownMs == 0 && d <= BULLET_RANGE_TILES * GameMap.TILE_SIZE) {
            fireBullet(target);
            bulletCooldownMs = BULLET_INTERVAL_MS;
            attacked = true;
        }
        if (hasThunder && lightningCooldownMs == 0 && d <= LIGHTNING_RANGE_TILES * GameMap.TILE_SIZE) {
            strike(target);
            lightningCooldownMs = LIGHTNING_INTERVAL_MS;
            attacked = true;
        }
        return attacked;
    }

    private void fireBullet(Enemy target) {
        float half = GameMap.TILE_SIZE / 2f;
        float gx = goose.getX() + half;
        float gy = goose.getY() + half;
        float dx = target.getCenterX() - gx;
        float dy = target.getCenterY() - gy;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        float dirX = len == 0f ? 1f : dx / len;
        float dirY = len == 0f ? 0f : dy / len;
        bullets.add(new Bullet(gx, gy, dirX, dirY, BULLET_SPEED, (BULLET_RANGE_TILES + 1) * GameMap.TILE_SIZE));
    }

    private void strike(Enemy target) {
        lightningX = target.getCenterX();
        lightningY = target.getCenterY();
        lightningFlashMs = LIGHTNING_FLASH_MS;
        target.hit(LIGHTNING_DAMAGE, Enemy.HitSource.LIGHTNING);
    }

    private void updateBullets(long deltaMs) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaMs);
            int row = (int) Math.floor(bullet.y / GameMap.TILE_SIZE);
            int col = (int) Math.floor(bullet.x / GameMap.TILE_SIZE);
            if (bullet.isSpent() || !map.isPassable(row, col)) {
                bullets.remove(i);
                continue;
            }
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (!enemy.isDead()
                        && distance(enemy.getCenterX(), enemy.getCenterY(), bullet.x, bullet.y) <= BULLET_HIT_DISTANCE) {
                    enemy.hit(BULLET_DAMAGE, Enemy.HitSource.BULLET);
                    bullets.remove(i);
                    break;
                }
            }
        }
    }

    private void updateExplosions(long deltaMs) {
        for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion explosion = explosions.get(i);
            explosion.elapsedMs += deltaMs;
            if (explosion.elapsedMs >= EXPLOSION_MS) {
                explosions.remove(i);
            }
        }
    }

    private void onPlayerDied() {
        stats.revive();
        bullets.clear();
        if (!isTeleporting()) {
            startTeleport(TeleportKind.HOME);
        } else if (!teleportArrived) {
            teleportKind = TeleportKind.HOME;
        }
    }

    private void reportHp() {
        int hp = stats.getHp();
        if (hp != reportedHp) {
            reportedHp = hp;
            if (hpListener != null) {
                hpListener.onHpChanged(hp, PlayerStats.MAX_HP);
            }
        }
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void drawEnemies(Canvas canvas, int tileSize) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            int ex = Math.round(enemy.getX()) - cameraX;
            int ey = Math.round(enemy.getY()) - cameraY;
            reusableDst.set(ex, ey, ex + tileSize, ey + tileSize);
            enemy.draw(canvas, reusableDst);
        }
    }

    private void drawCombatEffects(Canvas canvas) {
        effectPaint.setStyle(Paint.Style.FILL);
        effectPaint.setColor(0xFF9FEFFF);
        effectPaint.setAlpha(255);
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            canvas.drawCircle(bullet.x - cameraX, bullet.y - cameraY, 6f, effectPaint);
        }

        for (int i = 0; i < explosions.size(); i++) {
            Explosion explosion = explosions.get(i);
            float t = explosion.elapsedMs / (float) EXPLOSION_MS;
            float cx = explosion.x - cameraX;
            float cy = explosion.y - cameraY;
            float radius = EXPLOSION_RADIUS * (0.25f + 0.75f * t);
            effectPaint.setStyle(Paint.Style.FILL);
            effectPaint.setColor(0xFFFF7A2F);
            effectPaint.setAlpha((int) (110f * (1f - t)));
            canvas.drawCircle(cx, cy, radius, effectPaint);
            effectPaint.setStyle(Paint.Style.STROKE);
            effectPaint.setStrokeWidth(4f);
            effectPaint.setColor(0xFFFFD27A);
            effectPaint.setAlpha((int) (255f * (1f - t)));
            canvas.drawCircle(cx, cy, radius, effectPaint);
        }

        if (lightningFlashMs > 0) {
            float fade = lightningFlashMs / (float) LIGHTNING_FLASH_MS;
            effectPaint.setStyle(Paint.Style.STROKE);
            effectPaint.setStrokeWidth(4f);
            effectPaint.setColor(0xFFFFF27A);
            effectPaint.setAlpha((int) (255f * fade));
            int segments = LIGHTNING_JITTER.length - 1;
            float top = lightningY - 7f * GameMap.TILE_SIZE;
            for (int i = 0; i < segments; i++) {
                float y1 = top + (lightningY - top) * i / segments;
                float y2 = top + (lightningY - top) * (i + 1) / segments;
                canvas.drawLine(lightningX + LIGHTNING_JITTER[i] - cameraX, y1 - cameraY,
                        lightningX + LIGHTNING_JITTER[i + 1] - cameraX, y2 - cameraY, effectPaint);
            }
        }
    }

    private boolean isOverLiquid(float spriteX, float spriteY) {
        return map.isLiquidAt(spriteX + GameMap.TILE_SIZE / 2f, spriteY + GameMap.TILE_SIZE / 2f);
    }

    private void checkBirdRescues() {
        for (Iterator<Bird> it = idleBirds.iterator(); it.hasNext(); ) {
            Bird idle = it.next();
            if (idle.getRow() != goose.getRow() || idle.getCol() != goose.getCol()) {
                continue;
            }
            it.remove();
            idle.startFollowing();

            int leaderRow, leaderCol;
            if (followingBirds.isEmpty()) {
                leaderRow = goose.getRow();
                leaderCol = goose.getCol();
            } else {
                Bird lastInChain = followingBirds.get(followingBirds.size() - 1);
                leaderRow = lastInChain.getRow();
                leaderCol = lastInChain.getCol();
            }

            followingBirds.add(idle);
            chaseTargets.add(new int[]{leaderRow, leaderCol});

            onBirdRescued(idle.getRow(), idle.getCol());
        }
    }

    private void onBirdRescued(int spawnRow, int spawnCol) {
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        prefs.setBirdRescued(stageId, spawnRow, spawnCol);

        Ability ability = Ability.forStage(stageId);
        if (ability != null) {
            prefs.unlockAbility(ability);
            refreshPassiveAbilities();
            if (abilityUnlockedListener != null) {
                abilityUnlockedListener.onAbilityUnlocked(ability);
            }
        }
    }

    private void updateBirds(long deltaMs) {
        int leaderRow = goose.getRow();
        int leaderCol = goose.getCol();

        for (int i = 0; i < followingBirds.size(); i++) {
            Bird bird = followingBirds.get(i);
            int[] chaseTarget = chaseTargets.get(i);

            if (leaderRow != chaseTarget[0] || leaderCol != chaseTarget[1]) {
                bird.moveTowards(chaseTarget[0], chaseTarget[1]);
            }
            chaseTarget[0] = leaderRow;
            chaseTarget[1] = leaderCol;

            bird.update(deltaMs);
            bird.setSwimming(isOverLiquid(bird.getX(), bird.getY()));

            leaderRow = bird.getRow();
            leaderCol = bird.getCol();
        }
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

        drawBirds(canvas, idleBirds, tileSize);
        drawBirds(canvas, followingBirds, tileSize);
        drawEnemies(canvas, tileSize);
        drawSonar(canvas, visibleW, visibleH);

        if (goose != null) {
            int screenX = Math.round(goose.getX()) - cameraX;
            int screenY = Math.round(goose.getY()) - cameraY;
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }
        drawCombatEffects(canvas);

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

        float half = GameMap.TILE_SIZE / 2f;
        for (int i = 0; i < idleBirds.size(); i++) {
            Bird bird = idleBirds.get(i);
            float bx = bird.getX() + half;
            float by = bird.getY() + half;
            float dx = bx - sonarX;
            float dy = by - sonarY;
            float pingStart = (float) Math.sqrt(dx * dx + dy * dy) / SONAR_SPEED;
            float t = sonarElapsedMs - pingStart;
            if (t < 0f || t >= SONAR_PING_MS) {
                continue;
            }
            float phase = t / SONAR_PING_MS;
            sonarPaint.setAlpha((int) (255f * (1f - phase)));
            sonarPaint.setStrokeWidth(SONAR_STROKE);
            canvas.drawCircle(bx - cameraX, by - cameraY, GameMap.TILE_SIZE * (0.3f + 0.6f * phase), sonarPaint);
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

        if (cell.hasItem() && cell.item.hasSprite() && itemSheet != null) {
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
