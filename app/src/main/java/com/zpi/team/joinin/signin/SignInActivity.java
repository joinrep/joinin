package com.zpi.team.joinin.signin;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.ui.common.LoadProfilePhoto;
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
    public static final String PHOTO_SOURCE = "photo";
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

    private Intent launchApp;

    //Facebook stuff
    LoginButton fLoginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;

    private ProgressBar mBarLoading;
    private String mPersonId, mPersonName, mPersonMail, mPhotoSource;


    // TODO signout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        mBarLoading = (ProgressBar) findViewById(R.id.bar_loading);
        launchApp = new Intent(SignInActivity.this, MainActivity.class);
//        launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        fLoginButton = (LoginButton) findViewById(R.id.facebook_button);
//        fLoginButton.setReadPermissions(PERMISSIONS);
        callbackManager = CallbackManager.Factory.create();
//        fLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//
//                Log.d("SignInActivity", "onSuccess");
//
////                Profile profile = Profile.getCurrentProfile();
////                if(profile != null)
////                {
////
////                }
////                startActivity(launchApp);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//            }
//        });

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    Log.d("SiginInActivity", "mProfileTracker");
                    mPersonId = currentProfile.getId();
                    mPersonName = currentProfile.getFirstName() + " " + currentProfile.getLastName();
                    mPersonMail = null;
                    mPhotoSource = FACEBOOK;
                    lanuchActivity();
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


        Button googleBtn =  (Button) findViewById(R.id.google_button);
        googleBtn.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/catull-regular.ttf"));
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.google_button) {
                    if (InternetConnection.isAvailable(SignInActivity.this)) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        });


        Button facebookBtn =  (Button) findViewById(R.id.facebook_button);
        facebookBtn.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Facebook Letter Faces.ttf"));
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.facebook.login.widget.LoginButton btn = new LoginButton(SignInActivity.this);
                btn.performClick();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        authentication();
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            mPhotoSource = GOOGLE;
            lanuchActivity();
        }
    }

    private void lanuchActivity() {
        launchApp.putExtra(PERSON_ID, mPersonId);
        launchApp.putExtra(PERSON_NAME, mPersonName);
        launchApp.putExtra(PERSON_MAIL, mPersonMail);
        launchApp.putExtra(PHOTO_SOURCE, mPhotoSource);

        startActivity(launchApp);
        finish();

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
        }
    }
}




