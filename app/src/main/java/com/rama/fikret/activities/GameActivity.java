package com.rama.fikret.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;
import com.rama.fikret.managers.PrefsManager;
import com.rama.fikret.widgets.HudView;

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
            // Resume where the player last entered a map.
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

        gameView.setOnAbilityUnlockedListener(ability -> runOnUiThread(() -> hudView.refreshAbilities()));

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

    private final HudView.OnActionListener hudListener = new HudView.OnActionListener() {
        @Override
        public void onMenu() {
            startActivity(new Intent(GameActivity.this, Main.class));
            finish();
        }

        @Override
        public void onSleep() {
        }

        @Override
        public void onSonar() {
            gameView.sonar();
        }

        @Override
        public void onHeal() {
        }

        @Override
        public void onTeleport() {
            gameView.teleportRandom();
        }

        @Override
        public void onTeleportHome() {
            gameView.teleportHome();
        }

        @Override
        public void onMagic() {
        }

        @Override
        public void onCandle() {
        }

        @Override
        public void onThunder() {
        }

        @Override
        public void onDive() {
            gameView.dive();
        }
    };
}

