package com.rama.fikret.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rama.fikret.R;
import com.rama.fikret.game.Ability;
import com.rama.fikret.game.ItemIcon;
import com.rama.fikret.managers.PrefsManager;

/**
 * Slim ability bar shown under the map. It only holds the three abilities that remain
 * (sonar, teleport home, dive) and hides itself until at least one of them is unlocked.
 */
public class HudView extends LinearLayout {

    public interface OnActionListener {
        void onSonar();

        void onTeleportHome();

        void onDive();
    }

    private final PrefsManager prefs;
    private OnActionListener listener;

    private View btnSonar;
    private ImageView iconSonar;
    private View btnTeleportHome;
    private ImageView iconTeleportHome;
    private View btnDive;
    private ImageView iconDive;

    public HudView(Context context) {
        this(context, null);
    }

    public HudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PrefsManager.getInstance(context);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.hud, this, true);

        btnSonar = findViewById(R.id.hud_btn_sonar);
        iconSonar = findViewById(R.id.hud_icon_sonar);
        btnTeleportHome = findViewById(R.id.hud_btn_teleport_home);
        iconTeleportHome = findViewById(R.id.hud_icon_teleport_home);
        btnDive = findViewById(R.id.hud_btn_dive);
        iconDive = findViewById(R.id.hud_icon_dive);

        iconSonar.setImageDrawable(ItemIcon.SONAR.drawable(getResources()));
        iconTeleportHome.setImageDrawable(ItemIcon.TELEPORT_HOME.drawable(getResources()));
        iconDive.setImageDrawable(ItemIcon.DIVE.drawable(getResources()));

        btnSonar.setOnClickListener(v -> {
            if (listener != null) listener.onSonar();
        });
        btnTeleportHome.setOnClickListener(v -> {
            if (listener != null) listener.onTeleportHome();
        });
        btnDive.setOnClickListener(v -> {
            if (listener != null) listener.onDive();
        });

        refreshAbilities();
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void refreshAbilities() {
        boolean sonar = applyAbility(Ability.SONAR, btnSonar, iconSonar);
        boolean home = applyAbility(Ability.TELEPORT_HOME, btnTeleportHome, iconTeleportHome);
        boolean dive = applyAbility(Ability.DIVE_DEEP_WATER, btnDive, iconDive);
        setVisibility(sonar || home || dive ? VISIBLE : GONE);
    }

    private boolean applyAbility(Ability ability, View button, ImageView icon) {
        boolean unlocked = prefs.hasAbility(ability);
        icon.setVisibility(unlocked ? VISIBLE : INVISIBLE);
        button.setEnabled(unlocked);
        button.setClickable(unlocked);
        return unlocked;
    }
}
