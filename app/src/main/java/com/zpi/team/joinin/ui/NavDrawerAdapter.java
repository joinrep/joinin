package com.zpi.team.joinin.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;

/**
 * Created by Arkadiusz on 2015-03-06.
 */
public class NavDrawerAdapter extends ArrayAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = -1;
    private static final int TYPE_SUBHEADER = -2;
    private static final int TYPES_COUNT = 3;

    private Context mContext;
    private int[] mIcons;
    private int[] mTitles;
    public NavDrawerAdapter(Context context, int[] icons, int[] titles){
        super(context, 0);
        mContext = context;
        mIcons = icons;
        mTitles= titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.navdrawer_item, null);
                    ImageView icon = (ImageView)convertView.findViewById(R.id.nav_drawer_icon);
                    icon.setImageResource(mIcons[position]);
                    TextView title = (TextView)convertView.findViewById(R.id.nav_drawer_title);
                    title.setText(mTitles[position]);
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.navdrawer_separator, null);
                    break;
                case TYPE_SUBHEADER:
                    convertView = inflater.inflate(R.layout.navdrawer_subheader, null);
                    TextView subheader = (TextView)convertView.findViewById(R.id.navdrawer_subheader);
                    subheader.setText(mTitles[position]);
                    break;
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public int getItemViewType(int position) {
        int type = mIcons[position];
        if(type == TYPE_SEPARATOR)  return TYPE_SEPARATOR;
        else if(type == TYPE_SUBHEADER) return TYPE_SUBHEADER;
        else return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {
        return mIcons[position] == TYPE_SEPARATOR || mIcons[position] == TYPE_SUBHEADER  ? false : true;
    }
}
