package com.zpi.team.joinin.ui.participateevents;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.signin.InternetConnection;
import com.zpi.team.joinin.ui.main.EventsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-08.
 */
public class ParticipateEventsFragment extends Fragment {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_participate_events, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        //TODO wczytac przy pierwszym logowaniu i zapisac do lokalnej
        if(InternetConnection.isAvailable(getActivity())) {
            new LoadUpcomingEvents().execute();
            mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        }
        else
            Toast.makeText(getActivity(), "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();


    }

    private class LoadUpcomingEvents extends AsyncTask<Void, Void, List<Event>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.loading_events), true);
        }

        @Override
        protected List<Event> doInBackground(Void... args) {
            return new EventRepository().getByParticipant(SessionStorage.getInstance().getUser());
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            String upcoming = getResources().getString(R.string.upcoming);
            String past = getResources().getString(R.string.past);
            mViewPager.setAdapter(new MyEventsPagerAdapter(new String[]{upcoming, past}, events));

            mSlidingTabLayout.setViewPager(mViewPager);

            progressDialog.dismiss();
        }
    }

    class MyEventsPagerAdapter extends PagerAdapter {
        private String[] mPageTitle;
        private List<Event> mUpcomingEvents = new ArrayList<Event>();
        private List<Event> mHistoryEvents = new ArrayList<Event>();

        MyEventsPagerAdapter(String[] titles, List<Event> events){
            mPageTitle = titles;

            long now = System.currentTimeMillis();
            for (Event event : events) {
                if (event.getEndTime().getTimeInMillis() >= now) {
                    mUpcomingEvents.add(event);
                } else {
                    mHistoryEvents.add(0, event);
                }
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);

            TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            RecyclerView eventsList = (RecyclerView) view.findViewById(R.id.tabEventsList);
            eventsList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            eventsList.setLayoutManager(layoutManager);

            List<Event> events;
            long now = System.currentTimeMillis();
            switch (position) {
                case 0:
                    events = mUpcomingEvents;
                    break;
                case 1:
                    events = mHistoryEvents;
                    break;
                default:
                    events = new ArrayList<Event>();
            }

            ParticipateEventsRecyclerAdapter adapter = new ParticipateEventsRecyclerAdapter(getActivity(), events);
            eventsList.setAdapter(adapter);

            emptyView.setVisibility(eventsList.getAdapter().getItemCount() > 0 ? View.GONE : View.VISIBLE);
            eventsList.setVisibility(eventsList.getAdapter().getItemCount() > 0 ? View.VISIBLE : View.GONE);

            return view;
        }

        @Override
        public int getCount() {
            return mPageTitle.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitle[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
