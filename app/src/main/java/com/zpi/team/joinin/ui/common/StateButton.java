package com.zpi.team.joinin.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.zpi.team.joinin.R;

/**
 * Created by Arkadiusz on 2015-05-21.
 */
public class StateButton extends Button {

    public StateButton (Context context) {
        super(context);
    }

    public StateButton (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateButton (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setJoinState(){
       this.setText(this.getContext().getResources().getString(R.string.participate_event));
       this.setTextColor(this.getContext().getResources().getColor(R.color.black_87));
        this.setEnabled(true);
    }

    public void setLeaveState(){
        this.setText(this.getContext().getResources().getString(R.string.not_participate_event));
        this.setTextColor(this.getContext().getResources().getColor(R.color.colorPrimary));
        this.setEnabled(true);
    }

    public void setNoRoomState(){
        this.setText(this.getContext().getResources().getString(R.string.no_room));
        this.setTextColor(this.getContext().getResources().getColor(R.color.disabled));
        this.setEnabled(false);
    }

    public void setPastState(){
        this.setText(this.getContext().getResources().getString(R.string.took_place));
        this.setTextColor(this.getContext().getResources().getColor(R.color.disabled));
        this.setEnabled(false);
    }
}
