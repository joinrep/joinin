package com.zpi.team.joinin.ui.details;

import android.content.Context;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Arkadiusz on 2015-04-26.
 */

import com.google.android.gms.games.multiplayer.Participant;
import com.zpi.team.joinin.R;

import java.util.ArrayList;

public class DialogListAdapter extends ArrayAdapter {
    private Context mContext;
    private ArrayList<String> mParticipants;

    public DialogListAdapter(Context context, ArrayList<String> participants) {
        super(context, 0);
        mContext = context;
        mParticipants = participants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.dialog_participants_item, null);
            ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
//            RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
//            rounded.setCornerRadius(270f);
//            rounded.setAntiAlias(true);
//            photo.setImageDrawable(rounded);
            photo.setImageResource(R.drawable.ic_launcher);
            TextView title = (TextView) convertView.findViewById(R.id.title);
//            String nameAndSurname = mParticipants.get(position).getName() + " " + mParticipants.get(position).getSurname();
//            title.setText(nameAndSurname);
            title.setText(mParticipants.get(position));


        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mParticipants.size();
    }


}
