package com.zpi.team.joinin.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zpi.team.joinin.R;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> mNavDrawerItems;

    private int mCurrentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(
                    getResources().getColor(R.color.colorPrimaryDark));
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        prepareNavDrawerItems();

        mDrawerList = (ListView) findViewById(R.id.nav_drawer_list);
        NavDrawerAdapter adapter = new NavDrawerAdapter(this, mNavDrawerItems);
        mDrawerList.setAdapter(adapter);

        View header = View.inflate(this, R.layout.navdrawer_header, null);
        mDrawerList.addHeaderView(header, null, false);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new EventsFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        mCurrentPosition = 1;
        if(savedInstanceState != null)
            mCurrentPosition = savedInstanceState.getInt("menuPosition", 1);

//        selectMenuItem(1); // na start uruchamia pierwszy element menu. kiepski sposob
        syncToolbarTitleAndMenuItemCheckedState(mCurrentPosition);
        Log.d("onCreate", (String)mToolbar.getTitle());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("menuPosition", mCurrentPosition);
        Log.d("onsave", Integer.toString(mCurrentPosition));
    }

    private void prepareNavDrawerItems(){
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_events,R.string.navdrawer_events));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_participate_events,R.string.navdrawer_participate));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_my_events,R.string.navdrawer_myevents));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.TYPE_SEPARATOR));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.NO_ICON,R.string.navdrawer_subheader_favorites,NavDrawerItem.TYPE_SUBHEADER));
        // TODO change icon of Przeglądaj ketegorie
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_plus_circle,R.string.add_favorite_category));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_bike,R.string.category_event_bike));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.TYPE_SEPARATOR));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_settings,R.string.navdrawer_settings));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_help,R.string.navdrawer_help));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectMenuItem(position);
        }
    }

    private void selectMenuItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 1:
                setToolbarElevation(true);
                fragment = new EventsFragment();
                break;
            case 2:
                setToolbarElevation(true);
                fragment = new ParticipateEventsFragment();
                break;
            case 3:
                setToolbarElevation(false);
                fragment = new MyEventsFragment();
                break;
            case 6:
                setToolbarElevation(true);
                fragment = new CategoriesFragment();
                break;
            default:
                break;
        }



        if (fragment != null) {
            mCurrentPosition = position;
            Log.d("Bundle", Integer.toString(position));
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();

            syncToolbarTitleAndMenuItemCheckedState(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private void setToolbarElevation(boolean elevation){
        if(elevation)
            getSupportActionBar().setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        else
            getSupportActionBar().setElevation(0);
    }

    private void syncToolbarTitleAndMenuItemCheckedState(int position){
        mDrawerList.setItemChecked(position, true);
        CharSequence title = getResources().getString(mNavDrawerItems.get(--position).getTitle()); //listener is 1-based
        setTitle(title);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(title);
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
