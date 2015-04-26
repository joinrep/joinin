package com.zpi.team.joinin.ui.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.signin.InternetConnection;
import com.zpi.team.joinin.ui.myevents.MyEventsRecyclerAdapter;
import com.zpi.team.joinin.ui.myevents.SlidingTabLayout;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-25.
 */
public class MyEventsFragment extends EventsRecyclerFragment {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private OnToolbarElevationListener mOnToolbarElevationListener;

    @Override
    public int getType() {
        return OWN;
    }

    @Override
    public boolean isFloatingActionButtonVisible() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnToolbarElevationListener = (OnToolbarElevationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToolbarElevationListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnToolbarElevationListener.setToolbarElevation(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOnToolbarElevationListener.setToolbarElevation(false);
        super.inflateWithEvents();
        return inflater.inflate(R.layout.fragment_myevents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onCustomPostExecute(List<Event> events) {
        String upcoming = getResources().getString(R.string.upcoming);
        String past = getResources().getString(R.string.past);
        mViewPager.setAdapter(new MyEventsPagerAdapter(new String[]{upcoming, past}, events));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    class MyEventsPagerAdapter extends PagerAdapter {
        private String[] mPageTitle;
        private List<Event> mEvents;

        MyEventsPagerAdapter(String[] titles, List<Event> events) {
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
