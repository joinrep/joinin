package com.zpi.team.joinin.ui.common;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.zpi.team.joinin.R;

/**
 * Created by Arkadiusz on 2015-05-17.
 */
public class AnimatedConnectionDialog {
    private static RelativeLayout mDialogView;
    private static Button mRetry;
    private static View mViewToRise;

    public AnimatedConnectionDialog(Activity activity) {
        try {
            mDialogView = (RelativeLayout) activity.findViewById(R.id.view_nointernet);
            mRetry = (Button) mDialogView.findViewById(R.id.retry);

        }catch (NullPointerException n){
            throw new NullPointerException(activity.toString()
                    + " layout must include view_nointernet.xml layout.");
        }
    }

    public void setOnRetryListener(View.OnClickListener listener){
        mRetry.setOnClickListener(listener);
    }

    public  void setViewToRaise(View v){
        mViewToRise = v;

    }

    public  void clear(){
        mDialogView.setTranslationY(mDialogView.getHeight());
    }

    public  void startAnimation() {
        if(mViewToRise != null) {
            mViewToRise.animate().translationYBy(-mViewToRise.getTranslationY());
            Log.d("AnimatedCon", "startANimation(), " + mDialogView.getHeight() + " t: " + mDialogView.getTranslationY());
        }
        mDialogView.animate().translationYBy(mDialogView.getHeight());

        Log.d("AnimatedCon", "startANimation(), " + mDialogView.getHeight() + " t: " + mDialogView.getTranslationY());
    }

    public  void replayAnimation() {
        replayAnimation(1000);
    }

    public  void replayAnimation(int ms) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mViewToRise != null) {
                    mViewToRise.animate().translationYBy( -(mDialogView.getHeight() + mViewToRise.getTranslationY()));

                    Log.d("InternetConnection", "run(), !=null");
                }

                mDialogView.animate().translationYBy(-mDialogView.getHeight());
            }
        }, ms);
    }

}
