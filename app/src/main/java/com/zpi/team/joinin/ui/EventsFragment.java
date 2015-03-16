package com.zpi.team.joinin.ui;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-06.
 */
public class EventsFragment extends Fragment {

    private RecyclerView mEventsList;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        mEventsList = (RecyclerView) view.findViewById(R.id.eventsList);
        mEventsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEventsList.setLayoutManager(mLayoutManager);

        new LoadAllEvents().execute();

        return view;
    }

    private class LoadAllEvents extends AsyncTask<String, String, String> {
        List<Event> events;

        protected String doInBackground(String... args) {
            events = new EventRepository().getAll();
            return "dumb";

        }

        protected void onPostExecute(String s) {
            EventsRecyclerAdapter adapter = new EventsRecyclerAdapter(events);
            mEventsList.setAdapter(adapter);
        }
    }
}
