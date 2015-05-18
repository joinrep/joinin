package com.zpi.team.joinin.ui.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.repository.exceptions.EventFullException;

/**
 * Created by Arkadiusz on 2015-05-16.
 */
public class ToggleParticipate extends AsyncTask<Event, Void, Void> {
    SessionStorage storage = SessionStorage.getInstance();
    Context context;
    public ToggleParticipate(Context context){
        this.context = context;
    }
    protected Void doInBackground(Event... event) {
        try {
            new EventRepository().participate(event[0], storage.getUser(), event[0].getParticipate());

        } catch (EventFullException e) {
            Toast.makeText(context, R.string.event_full, Toast.LENGTH_LONG).show();
        }
        ;
        return null;
    }

}
