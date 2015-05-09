package com.zpi.team.joinin.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.zpi.team.joinin.signin.SignInActivity;
import com.zpi.team.joinin.ui.main.MainActivity;

import java.net.URISyntaxException;

/**
 * Created by Arkadiusz on 2015-05-09.
 */
public class MyPreferences {

    private static final String MY_PREFERENCES = "my_preferences";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final int PUT_INTENT_REQUEST = 1;
    public static final String SIGN_IN_INTENT = "sign_in_intent";
    private static final String LOGGED_AS = "logged_as";

    private static Context mContext;

    public static void setContext(Context context){
        mContext = context;
    }

    public static boolean isAlreadyLoggedIn() {
        return getReader().getBoolean(IS_LOGGED_IN, false);
    }

    public static void setAsLoggedOut() {
        getReader().edit().putBoolean(IS_LOGGED_IN, false).commit();
    }

    public static void setAsLoggedIn() {
        getReader().edit().putBoolean(IS_LOGGED_IN, true).commit();
    }

    public static void putIntent(Intent intent){
        String uriString = intent.toUri(PUT_INTENT_REQUEST);
        getReader().edit().putString(SIGN_IN_INTENT, uriString).commit();
    }

    public static Intent getIntent(String signInIntent) {
        Intent data = null;
        String intentString = getReader().getString(signInIntent, null);
        try {
            data = Intent.parseUri(intentString,0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getLoginSource(){
        return getReader().getString(LOGGED_AS, null);
    }

    public static void setLoginSource(String source){
        getReader().edit().putString(LOGGED_AS, source).commit();
    }

    private static SharedPreferences getReader() {
        return mContext.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    }
}
