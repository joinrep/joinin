package com.zpi.team.joinin.signin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.MyPreferences;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.ui.main.MainActivity;

public class SignInActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final String KEY_IN_RESOLUTION = "is_in_resolution";
    public final static String SHUT_SIGIN_ACTIVITY_REQUEST = "shut_activity";
    public final static String GOOGLE = "google";
    public final static String FACEBOOK = "fb";
    public static final String PERSON_NAME = "name";
    public static final String PERSON_ID = "id";
    public static final String PERSON_MAIL = "mail";
    private static final String[] PERMISSIONS = {"user_friends"};


    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    private Context mContext;
    private Intent signInData;

    //Facebook stuff
    LoginButton fLoginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;

    private TextView mSignWith;
    private Button mGoogleBtn, mFacebookBtn;
    private ProgressBar mBarLoading;
    private String mLoginSource, mPersonId, mPersonName, mPersonMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }

        mContext = getApplicationContext();
        MyPreferences.setContext(mContext);
        FacebookSdk.sdkInitialize(mContext);
        setContentView(R.layout.activity_signin);
        signInData = new Intent(SignInActivity.this, MainActivity.class);
        callbackManager = CallbackManager.Factory.create();


        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("SiginInActivity", "mProfileTracker");
                if (currentProfile != null) {
                    Log.d("SiginInActivity", "mProfileTracker");
                    mPersonId = currentProfile.getId();
                    mPersonName = currentProfile.getFirstName() + " " + currentProfile.getLastName();
                    mPersonMail = null;
                    mLoginSource = FACEBOOK;

                    keeepDataAndLaunchMainActivity();
                }
            }
        };

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Log.d("SiginInActivity", " mAccessTokenTracker");
                Profile.fetchProfileForCurrentAccessToken();
            }
        };


        mGoogleBtn = (Button) findViewById(R.id.google_button);
        mGoogleBtn.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/catull-regular.ttf"));
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.google_button) {

                    authentication();
                    if (InternetConnection.isAvailable(SignInActivity.this)) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        });

        final com.facebook.login.widget.LoginButton btn = new LoginButton(SignInActivity.this);

        LoginManager.getInstance().logOut();
        mFacebookBtn =  (Button) findViewById(R.id.facebook_button);
        mFacebookBtn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/fb-letter-faces.ttf"));
        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("SignInActivity", "btnCLicked");
                btn.performClick();
            }
        });



        mBarLoading = (ProgressBar) findViewById(R.id.bar_loading);
        mSignWith = (TextView) findViewById(R.id.sign_with);

        if(MyPreferences.isAlreadyLoggedIn()){
            Log.d("SignInActivity", "already logged in");
            hideSignInComponents();
            signInData = MyPreferences.getIntent(MyPreferences.SIGN_IN_INTENT);
            new PrepareContent().execute();
        }
    }

    private void hideSignInComponents(){
        mSignWith.setVisibility(View.GONE);
        mGoogleBtn.setVisibility(View.GONE);
        mFacebookBtn.setVisibility(View.GONE);
    }
    @Override
    protected void onStart() {
        super.onStart();
//
//        authentication();
    }

    private void authentication() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    protected void onStop() {
        // TODO signout - problemy z wylogowaniem u MK
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("SignInActivity", "onActivityREsult(), callbackManager");
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            mPersonName = currentPerson.getDisplayName();
            mPersonId = currentPerson.getId();
            mPersonMail = Plus.AccountApi.getAccountName(mGoogleApiClient);
            mLoginSource = GOOGLE;

            keeepDataAndLaunchMainActivity();
        }
    }

    private void keeepDataAndLaunchMainActivity() {
        Log.d("SignInActivity", "setting preferences...");
        MyPreferences.setAsLoggedIn();
        MyPreferences.setLoginSource(mLoginSource);
        saveAccountDataIntent();

        String lName = mPersonName.substring(mPersonName.lastIndexOf(' ') + 1);
        String fName = mPersonName.substring(0, mPersonName.lastIndexOf(' '));
        User loggedInUser = new User(fName, lName);
        if (mLoginSource == GOOGLE) {
            loggedInUser.setGoogleId(mPersonId);
        } else {
            loggedInUser.setFacebookId(mPersonId);
        }
        SessionStorage.getInstance().setUser(loggedInUser);

        Log.d("SignInActivity", "launching activity...");
        startActivity(signInData);
        finish();
    }

    private void saveAccountDataIntent(){
        signInData.putExtra(PERSON_ID, mPersonId);
        signInData.putExtra(PERSON_NAME, mPersonName);
        signInData.putExtra(PERSON_MAIL, mPersonMail);

        MyPreferences.putIntent(signInData);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    private class PrepareContent extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            mBarLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            mBarLoading.setVisibility(View.GONE);
            startActivity(signInData);
            finish();
        }
    }
}




