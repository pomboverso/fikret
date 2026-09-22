package com.rama.fikret.activities;

import android.app.Activity;
import android.os.Bundle;

import com.rama.fikret.R;
import com.rama.fikret.helpers.SystemBars;
import com.rama.fikret.managers.FontManager;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBars.applyInsets(findViewById(R.id.root));
        FontManager.apply(findViewById(R.id.root), FontManager.getJersey25(this));
    }
}
