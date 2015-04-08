package com.zpi.team.joinin.ui;

import android.app.Fragment;
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

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-08.
 */
public class MyEventsFragment extends Fragment {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myevents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        new LoadUpcomingEvents().execute();

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
    }

    private class LoadUpcomingEvents extends AsyncTask<Void, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Void... args) {
            return new EventRepository().getAll();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            String upcoming = getResources().getString(R.string.upcoming);
            String past = getResources().getString(R.string.past);
            mViewPager.setAdapter(new MyEventsPagerAdapter(new String[]{upcoming, past}, events));

            mSlidingTabLayout.setViewPager(mViewPager);

        }
    }

    class MyEventsPagerAdapter extends PagerAdapter {
        private String[] mPageTitle;
        private List<Event> mEvents;

        MyEventsPagerAdapter(String[] titles, List<Event> events){
            mPageTitle = titles;
            mEvents = events;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);

            RecyclerView eventsList = (RecyclerView) view.findViewById(R.id.tabEventsList);
            eventsList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            eventsList.setLayoutManager(layoutManager);

            MyEventsRecyclerAdapter adapter = new MyEventsRecyclerAdapter(getActivity(), mEvents);

            eventsList.setAdapter(adapter);

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
