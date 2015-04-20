package com.zpi.team.joinin.signin;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.ui.common.BitmapDecoder;
import com.zpi.team.joinin.ui.main.MainActivity;

public class SignInActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final String KEY_IN_RESOLUTION = "is_in_resolution";
    private static final String PERSON_NAME = "name";
    private static final String PERSON_ID = "id";
    private static final String PERSON_PHOTO_URL = "photo";
    private static final String PERSON_MAIL = "mail";
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



    // TODO signout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        launchApp = new Intent(SignInActivity.this, MainActivity.class);
        fLoginButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        fLoginButton.setReadPermissions(PERMISSIONS);
        callbackManager = CallbackManager.Factory.create();
        fLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startActivity(launchApp);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        Profile profile = Profile.getCurrentProfile();
        if(profile != null)
        {
            startActivity(launchApp);
        }


        /**TODO
         * po pierwszym logowaniu pokazywac tylko progress bar/
         *   => zmiana lanuchera w manifescie
         * */



        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    if (InternetConnection.isAvailable(SignInActivity.this)) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        });

        //dla testow
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.skip) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        startActivity(launchApp, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
//                        finish();
                    } else {
                        startActivity(launchApp);
//                        finish();
                    }

                }
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
            String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personId = currentPerson.getId();
            String personMail = Plus.AccountApi.getAccountName(mGoogleApiClient);

            launchApp.putExtra(PERSON_ID, personId);
            launchApp.putExtra(PERSON_NAME, personName);
            launchApp.putExtra(PERSON_PHOTO_URL, personPhotoUrl);
            launchApp.putExtra(PERSON_MAIL, personMail);
        }

        lanuchActivity();
    }

    private void lanuchActivity() {
        if (mGoogleApiClient.isConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(launchApp, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
//                        finish();
            } else {
                startActivity(launchApp);
//                        finish();
            }
        }
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
}




