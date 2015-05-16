package com.zpi.team.joinin.ui.common;

import android.os.AsyncTask;

import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;

/**
 * Created by Arkadiusz on 2015-05-16.
 */
public class ToggleParticipate extends AsyncTask<Event, Void, Void> {
    SessionStorage storage = SessionStorage.getInstance();

    protected Void doInBackground(Event... event) {
        new EventRepository().participate(event[0], storage.getUser(), event[0].getParticipate());
        return null;
    }

}
