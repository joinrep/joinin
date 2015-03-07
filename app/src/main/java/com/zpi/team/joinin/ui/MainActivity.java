package com.zpi.team.joinin.ui;

import android.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new EventsFragment();
    }
}
