package com.zpi.team.joinin.ui.details;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
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
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

public class DialogListAdapter extends ArrayAdapter {
    private Context mContext;
    private List<User> mParticipants;

    public DialogListAdapter(Context context, List<User> participants) {
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
            TextView title = (TextView) convertView.findViewById(R.id.title);

//            RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
//            rounded.setCornerRadius(270f);
//            rounded.setAntiAlias(true);
//            photo.setImageDrawable(rounded);
//            photo.setImageResource(R.drawable.ic_launcher);
            photo.setImageResource(R.drawable.ic_launcher);

            String nameAndSurname = mParticipants.get(position).getFirstName() + " " + mParticipants.get(position).getLastName();
            title.setText(nameAndSurname);
//            title.setText(mParticipants.get(position));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mParticipants.size();
    }


}
