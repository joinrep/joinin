package com.zpi.team.joinin.ui.main;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.signin.InternetConnection;
import com.zpi.team.joinin.ui.newevent.CreateEventActivity;
import com.zpi.team.joinin.ui.newevent.CreateEventFragment;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-06.
 */
public class EventsFragment extends Fragment {

    private static int CREATE_EVENT_REQUEST = 1;
    private List<Event> mEvents;
    private RecyclerView mEventsList;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO jesli zmieni sie cos w bazie ofc
                Log.d("EventsFragment", "refresh()");
                mEventsList.getAdapter().notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        View addEventButton = view.findViewById(R.id.add_event_button);
        if (Build.VERSION.SDK_INT >= 21) {
            addEventButton.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
                    outline.setOval(0, 0, diameter, diameter);
                }
            });
            addEventButton.setClipToOutline(true);
        }

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewEvent();
            }
        });

        if (InternetConnection.isAvailable(getActivity()))
            new LoadAllEvents().execute();
        else
            Toast.makeText(getActivity(), "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void addNewEvent() {
        startActivityForResult(new Intent(getActivity(), CreateEventActivity.class), CREATE_EVENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_EVENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
               Event newEvent =  SessionStorage.getInstance().getNewlyCreated();
               if(newEvent != null) mEvents.add(newEvent);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


    private class LoadAllEvents extends AsyncTask<Void, Void, List<Event>> {
        ProgressDialog progressDialog;

        //TODO crash przy zmianie orientacji
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.loading_events), true);
        }

        protected List<Event> doInBackground(Void... args) {
            mEvents = new EventRepository().getAll();
            return mEvents;
        }

        protected void onPostExecute(List<Event> events) {
            EventsRecyclerAdapter adapter = new EventsRecyclerAdapter(getActivity(), events);
            mEventsList.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}
