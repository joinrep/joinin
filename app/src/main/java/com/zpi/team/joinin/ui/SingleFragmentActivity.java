package com.zpi.team.joinin.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.zpi.team.joinin.R;
/**
 * Created by Arkadiusz on 2015-03-06.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity{
    protected abstract Fragment createFragment();

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    private static final int NAVDRAWER_ITEM_SEPARATOR = -1;
    private static final int NAVDRAWER_ITEM_SUBHEADER = -2;


    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_events,
            R.drawable.ic_participate_events,
            R.drawable.ic_my_events,
            NAVDRAWER_ITEM_SEPARATOR,
            NAVDRAWER_ITEM_SUBHEADER,
            R.drawable.ic_category_bike,
            NAVDRAWER_ITEM_SEPARATOR,
            R.drawable.ic_settings,
            R.drawable.ic_help
    };

    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_events,
            R.string.navdrawer_participate,
            R.string.navdrawer_myevents,
            NAVDRAWER_ITEM_SEPARATOR,
            R.string.navdrawer_subheader_favorites,
            R.string.category_event_bike,
            NAVDRAWER_ITEM_SEPARATOR,
            R.string.navdrawer_settings,
            R.string.navdrawer_help
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(
                    getResources().getColor(R.color.colorPrimaryDark));
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mToolbar.setTitle("po zamknieciu");
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mToolbar.setTitle("po otwarciu");
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList = (ListView)findViewById(R.id.nav_drawer_list);
        mDrawerList.setAdapter(new NavDrawerAdapter(this, NAVDRAWER_ICON_RES_ID, NAVDRAWER_TITLE_RES_ID ));
        View header = View.inflate(this, R.layout.navdrawer_header, null);
        mDrawerList.addHeaderView(header);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
}