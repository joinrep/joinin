package com.zpi.team.joinin.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.MyPreferences;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.CategoryRepository;
import com.zpi.team.joinin.repository.UserRepository;
import com.zpi.team.joinin.signin.LogOutDialog;
import com.zpi.team.joinin.signin.SignInActivity;
import com.zpi.team.joinin.ui.categories.CategoriesFragment;
import com.zpi.team.joinin.ui.common.LoadProfilePhoto;
import com.zpi.team.joinin.ui.events.AllEventsFragment;

import com.zpi.team.joinin.ui.events.MyOwnEventsFragment;

import com.zpi.team.joinin.ui.events.ByCategoryEventsFragment;
import com.zpi.team.joinin.ui.common.OnToolbarElevationListener;
import com.zpi.team.joinin.ui.events.ParticipateEventsFragment;

import com.zpi.team.joinin.ui.nav.NavDrawerAdapter;
import com.zpi.team.joinin.ui.nav.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnToolbarElevationListener, View.OnClickListener {

    private final static int ALL = 1;
    private final static int PARTICIPATE = 2;
    private final static int MYOWN = 3;
    public final static int ADD_CATEGORY_POSITION = 6;

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> mNavDrawerItems;
    private NavDrawerAdapter mNavDrawerAdapter;
    private View mHeader;
    private ImageButton mLogout;
    private BroadcastReceiver mLogoutReceiver;
    private int mCurrentPosition;
    private List<Category> favCategories = new ArrayList<Category>();

    private static Context mContext;

    public static Context getAppContext() {
        return MainActivity.mContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setEnterTransition(new Explode());

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

        prepareNavDrawerItems();
        mDrawerList = (ListView) findViewById(R.id.nav_drawer_list);
        setNavDrawerListWidth();
        mNavDrawerAdapter = new NavDrawerAdapter(this, mNavDrawerItems);

        mHeader = View.inflate(this, R.layout.navdrawer_header, null);
        inflateHeaderWithPersonData();
        mLogout = (ImageButton) mHeader.findViewById(R.id.logout);
        mLogout.setOnClickListener(this);

        mDrawerList.addHeaderView(mHeader, null, false);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerList.setAdapter(mNavDrawerAdapter);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new AllEventsFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        mCurrentPosition = 1;
        if (savedInstanceState != null)
            mCurrentPosition = savedInstanceState.getInt("menuPosition", 1);

        syncToolbarTitleAndMenuItemCheckedState(mCurrentPosition);
        new Initialize().execute();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zpi.team.joinin.ACTION_LOGOUT");
        mLogoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new LogOutDialog(mContext, MainActivity.this).show();
            }
        };
        registerReceiver(mLogoutReceiver, intentFilter);
    }

    //TODO prowizorka
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void inflateHeaderWithPersonData() {
        ImageView personPhoto = (ImageView) mHeader.findViewById(R.id.photo);
        TextView personName = (TextView) mHeader.findViewById(R.id.name);
        TextView personMail = (TextView) mHeader.findViewById(R.id.mail);

        Intent personData = getIntent();

        if (personData.getExtras() != null) {
            Log.d("MainActivity", "inflateHeaderWithPersonData(), logging in.");
            String id = personData.getStringExtra(SignInActivity.PERSON_ID);
            String name = personData.getStringExtra(SignInActivity.PERSON_NAME);
            String mail = personData.getStringExtra(SignInActivity.PERSON_MAIL);
            Log.d("MainActivity" , "getLoginSource(), " + MyPreferences.getLoginSource());
            new LoadProfilePhoto(personPhoto, this).execute(MyPreferences.getLoginSource(), id);
            personName.setText(name);
            if (mail == null) mail = "Zalogowany przez Facebook";
            personMail.setText(mail);
        } else
            Log.d("MainActivity", "inflateHeaderWithPersonData(), cannot log in.");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("menuPosition", mCurrentPosition);
    }

    private void setNavDrawerListWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float marginPixels = 56 * density;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
        params.width = outMetrics.widthPixels - (int) marginPixels;
        mDrawerList.setLayoutParams(params);
    }

    private void prepareNavDrawerItems() {
        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_events, R.string.navdrawer_events));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_participate_events, R.string.navdrawer_participate));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_my_events, R.string.navdrawer_myevents));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.TYPE_SEPARATOR));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.NO_ICON, R.string.navdrawer_subheader_favorites, NavDrawerItem.TYPE_SUBHEADER));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_plus_circle, R.string.add_favorite_category));
        mNavDrawerItems.add(new NavDrawerItem(NavDrawerItem.TYPE_SEPARATOR));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_settings, R.string.navdrawer_settings));
        mNavDrawerItems.add(new NavDrawerItem(R.drawable.ic_menu_help, R.string.navdrawer_help));
    }

    public void updateNavDrawerItems() {
        List<Category> categories = SessionStorage.getInstance().getCategories();
        if (categories != null) {
            int iter = 0;
            while (!mNavDrawerItems.get(iter++).getTitle().equals(getResources().getString(R.string.add_favorite_category)))
                ;
            for (int i = 0; i < favCategories.size(); i++) {
                mNavDrawerItems.remove(iter);
            }
            favCategories = new ArrayList<Category>();
            for (Category category : categories) {
                if (category.isUserFavorite()) {
                    favCategories.add(category);
                    mNavDrawerItems.add(iter++, new NavDrawerItem(category.getIconId(), category.getName()));
                }
            }

            mNavDrawerAdapter.notifyDataSetChanged();
            mDrawerList.setAdapter(mNavDrawerAdapter);
            syncToolbarTitleAndMenuItemCheckedState(mCurrentPosition);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.logout:
                Log.d("MainActivity", " onClick(), loggin out");
                logout();
                break;
        }
    }

    private void logout(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.zpi.team.joinin.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
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
            case ALL:
                fragment = new AllEventsFragment();
                break;
            case PARTICIPATE:
                fragment = new ParticipateEventsFragment();
                break;
            case MYOWN:
                fragment = new MyOwnEventsFragment();
                break;
            case ADD_CATEGORY_POSITION:
                if (mCurrentPosition == position) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;
                }
                fragment = new CategoriesFragment();
                break;
            default:
                if (position > ADD_CATEGORY_POSITION && position <= ADD_CATEGORY_POSITION + favCategories.size()) {
                    Log.d("click", favCategories.get(position - (ADD_CATEGORY_POSITION + 1)).getName());
                    setToolbarElevation(true);
                    fragment = new ByCategoryEventsFragment().setCategory(favCategories.get(position - (ADD_CATEGORY_POSITION + 1)));
                }
                break;
        }

        switchFragment(fragment, position);
    }

    public void switchFragment(Fragment fragment, int position) {
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

    public void setToolbarElevation(boolean elevation) {
        if (elevation)
            getSupportActionBar().setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        else
            getSupportActionBar().setElevation(0);
    }

    private void syncToolbarTitleAndMenuItemCheckedState(int position) {
        mDrawerList.setItemChecked(position, true);
        CharSequence title = mNavDrawerItems.get(--position).getTitle(); //listener is 1-based
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
        menu.findItem(R.id.action_filter).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLogoutReceiver);
    }

    // Initialization
    private class Initialize extends AsyncTask<String, Void, Void> {
        SessionStorage storage = SessionStorage.getInstance();
        List<Category> categories;

        protected Void doInBackground(String... args) {
            // TODO set real user in storage

            storage.setUser(new UserRepository().loginUser(storage.getUser()));

            categories = new CategoryRepository().getByUser(storage.getUser());
            // resolve categories icon id
            for (Category category : categories) {
                category.setIconId(MainActivity.this.getResources().getIdentifier(category.getIconPath(), "drawable", MainActivity.this.getPackageName()));
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            storage.setCategories(categories);
            updateNavDrawerItems();
        }
    }
}
