package com.zpi.team.joinin.ui.nav;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;

import java.util.ArrayList;

/**
 * Created by Arkadiusz on 2015-03-06.
 */
public class NavDrawerAdapter extends ArrayAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = -1;
    private static final int TYPE_SUBHEADER = -2;
    private static final int TYPES_COUNT = 3;

    private Context mContext;
    private ArrayList<NavDrawerItem> mNavDrawerItems;

    public NavDrawerAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        super(context, 0);
        mContext = context;
        mNavDrawerItems = navDrawerItems;
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
                    icon.setImageResource(mNavDrawerItems.get(position).getIcon());
                    TextView title = (TextView)convertView.findViewById(R.id.nav_drawer_title);
                    title.setText(mNavDrawerItems.get(position).getTitle());
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.navdrawer_separator, null);
                    break;
                case TYPE_SUBHEADER:
                    convertView = inflater.inflate(R.layout.navdrawer_subheader, null);
                    TextView subheader = (TextView)convertView.findViewById(R.id.navdrawer_subheader);
                    subheader.setText(mNavDrawerItems.get(position).getTitle());
                    break;
            }
        } else {
            // TODO MK: ostatnia kategoria ma nie nullowego convertView - do zbadania w dodawaniu ketogorii do navbara
            if (type == TYPE_ITEM) {
                ImageView icon = (ImageView)convertView.findViewById(R.id.nav_drawer_icon);
                icon.setImageResource(mNavDrawerItems.get(position).getIcon());
                TextView title = (TextView)convertView.findViewById(R.id.nav_drawer_title);
                title.setText(mNavDrawerItems.get(position).getTitle());
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mNavDrawerItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNavDrawerItems.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {
        int type = mNavDrawerItems.get(position).getType();
        return type == TYPE_SEPARATOR || type == TYPE_SUBHEADER  ? false : true;
    }
}
