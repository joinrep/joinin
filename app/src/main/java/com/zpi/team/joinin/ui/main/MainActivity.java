package com.zpi.team.joinin.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
import android.view.MenuItem;
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
import com.zpi.team.joinin.ui.common.OnToolbarModificationListener;
import com.zpi.team.joinin.ui.events.AllEventsFragment;
import com.zpi.team.joinin.ui.events.ByCategoryEventsFragment;
import com.zpi.team.joinin.ui.events.EventsFilter;
import com.zpi.team.joinin.ui.events.EventsRecyclerFragment;
import com.zpi.team.joinin.ui.events.EventsSorter;
import com.zpi.team.joinin.ui.events.MyOwnEventsFragment;
import com.zpi.team.joinin.ui.events.ParticipateEventsFragment;
import com.zpi.team.joinin.ui.nav.NavDrawerAdapter;
import com.zpi.team.joinin.ui.nav.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnToolbarModificationListener, View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private final String TAG = "MainActivity";
    private final static int ALL = 1;
    private final static int PARTICIPATE = 2;
    private final static int MYOWN = 3;
    public final static int ADD_CATEGORY_POSITION = 6;

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private boolean mSortFilterMenuVisibility;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> mNavDrawerItems;
    private NavDrawerAdapter mNavDrawerAdapter;
    private View mHeader;
    private ImageButton mLogout;
    private Menu mMenu;
    private BroadcastReceiver mLogoutReceiver;
    private int mCurrentPosition;
    private List<Category> favCategories = new ArrayList<Category>();
    Fragment mCurrentFragment;
    private boolean sortArrowPointsDown = true;

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
        mToolbar.setOnMenuItemClickListener(this);
        mSortFilterMenuVisibility = true;

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
        mCurrentFragment = fm.findFragmentById(R.id.fragmentContainer);
        if (mCurrentFragment == null) {
            mCurrentFragment = new AllEventsFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, mCurrentFragment)
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
            Log.d("MainActivity", "getLoginSource(), " + MyPreferences.getLoginSource());
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
        switch (id) {
            case R.id.logout:
                Log.d("MainActivity", " onClick(), loggin out");
                logout();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (mCurrentFragment instanceof EventsRecyclerFragment) {
            EventsRecyclerFragment fragment = (EventsRecyclerFragment) mCurrentFragment;
            menuItem.setChecked(!menuItem.isChecked());
            switch (menuItem.getItemId()) {
                case R.id.action_filter_full:
                case R.id.action_filter_free:
                    Log.d("Filter", "");
                    MenuItem fullItem = mMenu.findItem(R.id.action_filter_full);
                    MenuItem freeItem = mMenu.findItem(R.id.action_filter_free);
                    if (fullItem.isChecked()) {
                        if (freeItem.isChecked()) {
                            fragment.filter(EventsFilter.HIDE_FULL_ONLY_FREE);
                        } else {
                            fragment.filter(EventsFilter.HIDE_FULL);
                        }
                    } else {
                        if (freeItem.isChecked()) {
                            fragment.filter(EventsFilter.ONLY_FREE);
                        } else {
                            fragment.filter(EventsFilter.SHOW_ALL);
                        }
                    }
                    break;
                case R.id.action_sort_alpha:
                    fragment.sort(EventsSorter.ALPHABETICAL);
                    setSortIcon(R.id.action_sort_alpha);
                    break;
                case R.id.action_sort_create:
                    fragment.sort(EventsSorter.CREATE_DATE);
                    setSortIcon(R.id.action_sort_create);
                    break;
                case R.id.action_sort_start:
                    fragment.sort(EventsSorter.START_DATE);
                    setSortIcon(R.id.action_sort_start);
            }
        }
        return false;
    }

    private void setSortIcon(int menuItemId) {
        MenuItem sortMenuItem = mMenu.findItem(menuItemId);
        boolean hasIcon = sortMenuItem.getIcon() != null;
        clearSortIcons();

        if (hasIcon && sortArrowPointsDown) {
            sortMenuItem.setIcon(R.drawable.ic_arrow_up);
            sortArrowPointsDown = false;
        } else {
            sortMenuItem.setIcon(R.drawable.ic_arrow_down);
            sortArrowPointsDown = true;
        }
        sortMenuItem.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
    }

    private void clearSortIcons() {
        int[] menuItemIds = new int[] {R.id.action_sort_alpha, R.id.action_sort_start, R.id.action_sort_create};
        for(int menuItemId : menuItemIds) {
            MenuItem menuItem = mMenu.findItem(menuItemId);
            menuItem.setIcon(null);
        }
    }

    private void logout() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.zpi.team.joinin.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectNavDrawerItem(position);
        }
    }

    private void selectNavDrawerItem(int position) {
        mCurrentFragment = null;
        switch (position) {
            case ALL:
                mCurrentFragment = new AllEventsFragment();
                break;
            case PARTICIPATE:
                mCurrentFragment = new ParticipateEventsFragment();
                break;
            case MYOWN:
                mCurrentFragment = new MyOwnEventsFragment();
                break;
            case ADD_CATEGORY_POSITION:
                if (mCurrentPosition == position) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;
                }
                mCurrentFragment = new CategoriesFragment();
                break;
            default:
                if (position > ADD_CATEGORY_POSITION && position <= ADD_CATEGORY_POSITION + favCategories.size()) {
                    setToolbarElevation(true);
                    mCurrentFragment = new ByCategoryEventsFragment().setCategory(favCategories.get(position - (ADD_CATEGORY_POSITION + 1)));
                }
                break;
        }

        switchFragment(mCurrentFragment, position);
    }

    public void switchFragment(Fragment fragment, int position) {
        if (fragment != null) {
            mCurrentFragment = fragment;
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

    public void setSortFilterIconsVisibility(boolean state) {
        mSortFilterMenuVisibility = state;
        invalidateOptionsMenu();
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
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem sortMenuItem = mMenu.findItem(R.id.action_sort_create);
        sortMenuItem.setIcon(R.drawable.ic_arrow_down);
        sortMenuItem.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        sortArrowPointsDown = true;
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        MenuItem sort = menu.findItem(R.id.action_sort);
        MenuItem filter = menu.findItem(R.id.action_filter);
        boolean visibility = mSortFilterMenuVisibility && !drawerOpen;
            sort.setVisible(visibility);
            filter.setVisible(visibility);
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
            // TODO set real user in storage, wywalić marka kosa
            User userToLogIn = storage.getUser();
            if (userToLogIn == null) {
                userToLogIn = new User("", "");
                userToLogIn.setFacebookId("999788396717633");
            }
            storage.setUser(new UserRepository().loginUser(userToLogIn));

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
