package com.zpi.team.joinin.ui;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Outline;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-06.
 */
public class EventsFragment extends Fragment {

    private static int CREATE_EVENT_REQUEST = 1;
    private RecyclerView mEventsList;

    private RecyclerView.LayoutManager mLayoutManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view = inflater.inflate(R.layout.fragment_events, container, false);
//        } else {
//            view = super.onCreateView(inflater, container, savedInstanceState);
//        }

        mEventsList = (RecyclerView) view.findViewById(R.id.eventsList);
        mEventsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEventsList.setLayoutManager(mLayoutManager);


        View addEventButton = view.findViewById(R.id.add_event_button);

        addEventButton.setOutlineProvider(new ViewOutlineProvider() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getOutline(View view, Outline outline) {
                int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
                outline.setOval(0, 0, diameter, diameter);
            }
        });
        addEventButton.setClipToOutline(true);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewEvent();
            }
        });

        new LoadAllEvents().execute();

        return view;
    }

    private void addNewEvent() {
            startActivityForResult(new Intent(getActivity(), CreateEventActivity.class), CREATE_EVENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_EVENT_REQUEST) {
            if(resultCode == Activity.RESULT_OK){

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private class LoadAllEvents extends AsyncTask<String, String, String> {
        List<Event> events;

        protected String doInBackground(String... args) {
            events = new EventRepository().getAll();
            return "dumb";

        }

        protected void onPostExecute(String s) {
            EventsRecyclerAdapter adapter = new EventsRecyclerAdapter(getActivity().getApplicationContext(), events);
            mEventsList.setAdapter(adapter);
        }
    }
}
