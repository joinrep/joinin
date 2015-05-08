package com.zpi.team.joinin.ui.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.ui.common.OnToolbarElevationListener;
import com.zpi.team.joinin.ui.details.InDetailEventActivity;
import com.zpi.team.joinin.ui.details.InDetailEventFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-25.
 */
public class MyOwnEventsFragment extends EventsRecyclerFragment {
    private static int INDETAIL_EVENT_REQUEST = 2;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private OnToolbarElevationListener mOnToolbarElevationListener;

    @Override
    public int getType() {
        return MY_OWN;
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
        mViewPager.setAdapter(new EventsPagerAdapter(getActivity(), new String[]{upcoming, past}, events));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INDETAIL_EVENT_REQUEST) {
            PagerAdapter adapter = mViewPager.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
}
