package com.zpi.team.joinin.signin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.MyPreferences;
import com.zpi.team.joinin.ui.main.MainActivity;

/**
 * Created by Arkadiusz on 2015-05-09.
 */
public class LogOutDialog {
    Context mContext;
    Activity mCallerActivity;

    public LogOutDialog(Context context, Activity activity){
        mContext = context;
        mCallerActivity = activity;
    }

    public void show(){
        onCreateDialog().show();
    }

    private  Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCallerActivity);
        builder.setMessage(R.string.are_u_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyPreferences.setContext(mContext);
                        if(MyPreferences.getLoginSource() == "fb")
                            LoginManager.getInstance().logOut();
                        MyPreferences.setAsLoggedOut();
                        mCallerActivity.startActivity(new Intent(mCallerActivity, SignInActivity.class));
                        mCallerActivity.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
