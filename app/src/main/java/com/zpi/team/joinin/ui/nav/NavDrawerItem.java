package com.zpi.team.joinin.ui.nav;

import com.zpi.team.joinin.ui.main.MainActivity;

/**
 * Created by Arkadiusz on 2015-03-08.
 */
public class NavDrawerItem {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = -1;
    public static final int TYPE_SUBHEADER = -2;
    public static final int NO_ICON= -3;

    private int mTitle;
    private String mPlainTitle;
    private int mIcon;
    private int mType;

    public NavDrawerItem(int icon, int title, int type){
        mIcon = icon;
        mTitle = title;
        mType = type;
        mPlainTitle = null;
    }

    public NavDrawerItem(int icon, String title, int type){
        mIcon = icon;
        mTitle = 0;
        mType = type;
        mPlainTitle = title;
    }

    public NavDrawerItem(int icon, int title){
        this(icon, title, TYPE_ITEM);
    }
    public NavDrawerItem(int icon, String title){
        this(icon, title, TYPE_ITEM);
    }

    public NavDrawerItem(int type){
        assert type == TYPE_SEPARATOR;

        mIcon =  -1;
        mTitle = -1;
        mType = type;
        mPlainTitle = "";
    }

    public int getType() {return mType;}

    public int getIcon() {return mIcon;}

    public String getTitle() {
        if (mPlainTitle == null) {
            return MainActivity.getAppContext().getResources().getString(mTitle);
        } else {
            return mPlainTitle;
        }
    }
}
