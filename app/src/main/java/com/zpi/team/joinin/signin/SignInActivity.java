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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.ui.BitmapDecoder;
import com.zpi.team.joinin.ui.MainActivity;

public class SignInActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final String KEY_IN_RESOLUTION = "is_in_resolution";
    private static final String PERSON_NAME = "name";
    private static final String PERSON_ID = "id";
    private static final String PERSON_PHOTO_URL = "photo";
    private static final String PERSON_MAIL = "mail";

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

    // TODO signout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        setContentView(R.layout.activity_signin);

        /**TODO
         * po pierwszym logowaniu pokazywac tylko progress bar/
         *   => zmiana lanuchera w manifescie
         * */
        launchApp = new Intent(SignInActivity.this, MainActivity.class);
        ImageView bg = (ImageView) findViewById(R.id.sign_in_background);
        bg.setImageBitmap(BitmapDecoder.decodeSampledBitmapFromResource(getResources(), R.drawable.signin_background, 300, 500));

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button && mGoogleApiClient.isConnected()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(launchApp, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
//                        finish();
                    }
                    else {
                        startActivity(launchApp);
//                        finish();
                    }

                }
            }
        });

        //dla testow
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.skip) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(launchApp, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
//                        finish();
                    }
                    else {
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
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (InternetConnection.isAvailable(this))
            mGoogleApiClient.connect();
        else
            Toast.makeText(this, "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();
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
