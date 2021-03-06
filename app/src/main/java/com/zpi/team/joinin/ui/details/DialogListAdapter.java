package com.zpi.team.joinin.ui.details;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Arkadiusz on 2015-04-26.
 */

import com.google.android.gms.games.multiplayer.Participant;
import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.signin.SignInActivity;
import com.zpi.team.joinin.ui.common.LoadProfilePhoto;

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
            User user = mParticipants.get(position);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dialog_participants_item, null);

            final ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.bar_photo);
            bar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
            final ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
            TextView title = (TextView) convertView.findViewById(R.id.title);

            String source = user.getSource();
            String id = user.getLoginId();

            Log.d("DialogListAdapter", source + ", " + user.getLoginId());
            new AsyncTask<String, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    bar.setVisibility(View.VISIBLE);
                    photo.setVisibility(View.GONE);
                }

                @Override
                protected Void doInBackground(String... params) {
                    new LoadProfilePhoto(photo, mContext).execute(params[0], params[1]);
                    return  null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    bar.setVisibility(View.GONE);
                    photo.setVisibility(View.VISIBLE);
                }
            }.execute(source, id);

            String nameAndSurname = user.getFirstName() + " " + user.getLastName();
            title.setText(nameAndSurname);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mParticipants.size();
    }


}
