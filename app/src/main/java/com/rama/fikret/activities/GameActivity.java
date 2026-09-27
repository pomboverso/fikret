package com.rama.fikret.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;
import com.rama.fikret.widgets.HudView;

/**
 * Hosts the game surface fullscreen, with the HUD (see HudView) docked to
 * the bottom on top of it. GameView still owns the loop, the map and the
 * goose - the HUD is just an overlay that fires callbacks into this
 * activity (see the OnActionListener below).
 *
 * Pass which stage to load via EXTRA_STAGE, e.g.:
 *   Intent intent = new Intent(this, GameActivity.class);
 *   intent.putExtra(GameActivity.EXTRA_STAGE, Maps.NEXT_STAGE);
 *   startActivity(intent);
 * Omitting the extra loads Maps.MEADOW.
 */
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

        int stageId = getIntent().getIntExtra(EXTRA_STAGE, Maps.ARCTIC);
        gameView = new GameView(this, stageId);

        FrameLayout root = new FrameLayout(this);
        root.addView(gameView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        hudView = new HudView(this);
        FrameLayout.LayoutParams hudParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        root.addView(hudView, hudParams);
        hudView.setOnActionListener(hudListener);

        // checkBirdRescues()/onBirdRescued() run on GameThread, off the UI
        // thread (see GameView's comment there) - runOnUiThread() is what
        // makes it safe to touch hudView from that callback.
        gameView.setOnAbilityUnlockedListener(ability -> runOnUiThread(() -> hudView.refreshAbilities()));

        setContentView(root);
        gameView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Picks up any ability unlocked since the HUD was last shown (e.g.
        // rescuing a bird while this activity was paused).
        hudView.refreshAbilities();
    }

    // TODO: most of these just have their box wired up for now - hook each
    // one into the real gameplay action (GameView/Goose/PrefsManager) when
    // that logic exists.
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
        }

        @Override
        public void onHeal() {
        }

        @Override
        public void onTeleport() {
        }

        @Override
        public void onTeleportHome() {
        }

        @Override
        public void onMagic() {
        }

        @Override
        public void onCandle() {
        }

        @Override
        public void onDiamondSkin() {
        }

        @Override
        public void onThunder() {
        }

        @Override
        public void onDive() {
        }
    };
}

