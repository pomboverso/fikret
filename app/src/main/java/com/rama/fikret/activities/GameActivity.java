package com.rama.fikret.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rama.fikret.economy.GameState;
import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;
import com.rama.fikret.managers.PrefsManager;
import com.rama.fikret.widgets.HudView;

/** The map. Only used to walk between farms and to see the birds of the managers you hired. */
public class GameActivity extends Activity {
    public static final String EXTRA_STAGE = "stage";

    private GameView gameView;
    private HudView hudView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int stageId;
        int[] startPosition = null;
        if (getIntent().hasExtra(EXTRA_STAGE)) {
            stageId = getIntent().getIntExtra(EXTRA_STAGE, Maps.BEACH);
        } else {
            // Resume where the player was.
            PrefsManager prefs = PrefsManager.getInstance(this);
            stageId = prefs.getCurrentStage(Maps.BEACH);
            startPosition = prefs.getArrivalPosition();
        }
        gameView = new GameView(this, stageId, startPosition);
        hudView = new HudView(this);
        hudView.setOnActionListener(hudListener);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(gameView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(hudView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(root);
        gameView.requestFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (gameView != null && gameView.handleKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hudView.refreshAbilities();
    }

    @Override
    protected void onPause() {
        super.onPause();
        int[] position = gameView.getGoosePosition();
        if (position != null) {
            PrefsManager.getInstance(this).setArrivalPosition(position[0], position[1]);
        }
        GameState.get(this).save();
    }

    private final HudView.OnActionListener hudListener = new HudView.OnActionListener() {
        @Override
        public void onSonar() {
            gameView.sonar();
        }

        @Override
        public void onTeleportHome() {
            gameView.teleportHome();
        }

        @Override
        public void onDive() {
            gameView.dive();
        }
    };
}
