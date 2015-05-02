package com.zpi.team.joinin.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.ImageRequest;
import com.facebook.login.widget.ProfilePictureView;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.CategoryRepository;
import com.zpi.team.joinin.ui.categories.CategoriesFragment;
import com.zpi.team.joinin.ui.common.LoadProfilePhoto;
import com.zpi.team.joinin.ui.events.AllEventsFragment;
import com.zpi.team.joinin.ui.common.BitmapDecoder;

import com.zpi.team.joinin.ui.events.MyOwnEventsFragment;

import com.zpi.team.joinin.ui.events.ByCategoryEventsFragment;
import com.zpi.team.joinin.ui.common.OnToolbarElevationListener;
import com.zpi.team.joinin.ui.events.ParticipateEventsFragment;

import com.zpi.team.joinin.ui.nav.NavDrawerAdapter;
import com.zpi.team.joinin.ui.nav.NavDrawerItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnToolbarElevationListener {

    public final static int ADD_CATEGORY_POSITION = 6;

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> mNavDrawerItems;
    private NavDrawerAdapter mNavDrawerAdapter;

    private int mCurrentPosition;
    private List<Category> favCategories = new ArrayList<Category>();

    private static Context context;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    ProfilePictureView profilePicture;
    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
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

        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        prepareNavDrawerItems();

        mDrawerList = (ListView) findViewById(R.id.nav_drawer_list);
        setNavDrawerListWidth();
        mNavDrawerAdapter = new NavDrawerAdapter(this, mNavDrawerItems);


        header = View.inflate(this, R.layout.navdrawer_header, null);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                inflateWithPersonData(header);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // On AccessToken changes fetch the new profile which fires the event on
                // the ProfileTracker if the profile is different
                Profile.fetchProfileForCurrentAccessToken();
            }
        };

        inflateWithPersonData(header);
        mDrawerList.addHeaderView(header, null, false);
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
    }

    private void inflateWithPersonData(View header) {
        ImageView personPhoto = (ImageView) header.findViewById(R.id.photo);
        TextView personName = (TextView) header.findViewById(R.id.name);
        TextView personMail = (TextView) header.findViewById(R.id.mail);

        Intent personData = getIntent();
        Profile.fetchProfileForCurrentAccessToken();
        Profile facebookProfile = Profile.getCurrentProfile();
        //TODO przy pierwszym logowaniu zapisac lokalnie

        if (facebookProfile != null) {
            Log.d("signin", "facebooklogin");
            String id = facebookProfile.getId();
            String fname = facebookProfile.getFirstName();
            String lname = facebookProfile.getLastName();
            String personPhotoUrl = "https://graph.facebook.com/" + id + "/picture?type=large";

            new LoadProfilePhoto(personPhoto, this).execute(personPhotoUrl);
            personName.setText(fname + " " + lname);
            personMail.setText("Zalogowany przez Facebook");

        } else if (personData.getExtras() != null) {

            Log.d("signin", "googlelogin");
            String id = personData.getStringExtra("id");
            String personPhotoUrl = personData.getStringExtra("photo");
            String name = personData.getStringExtra("name");
            String mail = personData.getStringExtra("mail");

            new LoadProfilePhoto(personPhoto, this).execute(null, id);

            personName.setText(name);
            personMail.setText(mail);
        } else {
            Log.d("signin", "cannot log in from facebook/google+");
        }

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
                fragment = new AllEventsFragment();
                break;
            case 2:
                fragment = new ParticipateEventsFragment();
                break;
            case 3:
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
        return super.onPrepareOptionsMenu(menu);
    }


    // Initialization
    private class Initialize extends AsyncTask<String, Void, Void> {
        SessionStorage storage = SessionStorage.getInstance();
        List<Category> categories;

        protected Void doInBackground(String... args) {
            // TODO set real user in storage
            storage.setUser(new User(1, "Marek", "Kos"));


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
