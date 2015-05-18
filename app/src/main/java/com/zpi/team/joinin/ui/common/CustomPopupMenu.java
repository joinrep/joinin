package com.zpi.team.joinin.ui.common;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;

import com.zpi.team.joinin.R;

/**
 * Created by Arkadiusz on 2015-05-15.
 */
public class CustomPopupMenu {
    Context mContext;
    ListPopupWindow mPopupMenu;

    public CustomPopupMenu(Context context){
        mPopupMenu = new ListPopupWindow(context);
        mContext = context;

        customize();
    }

    public void setAdapter(ArrayAdapter adapter){
        mPopupMenu.setAdapter(adapter);
    }

    private void customize(){
        float density = mContext.getResources().getDisplayMetrics().density;
        float marginPixels = -40 * density;
        float width = 112 * density;
        mPopupMenu.setWidth((int) width);
        mPopupMenu.setVerticalOffset((int) marginPixels);
        mPopupMenu.setHorizontalOffset(-68 * (int) density);
    }

    public void setAnchorView(View v){
        mPopupMenu.setAnchorView(v);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mPopupMenu.setOnItemClickListener(listener);
    }

    public void show(){
        mPopupMenu.show();
    }

    public void dismiss(){
        mPopupMenu.dismiss();
    }

}
