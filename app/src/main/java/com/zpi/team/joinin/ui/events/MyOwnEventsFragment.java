package com.zpi.team.joinin.ui.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.ui.common.OnToolbarModificationListener;
import com.zpi.team.joinin.ui.newevent.CreateEventActivity;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-25.
 */
public class MyOwnEventsFragment extends EventsRecyclerFragment {
    private final String TAG = "MyOwnEventsFragment";
    private static int INDETAIL_EVENT_REQUEST = 2;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private TextView mEmptyView;
    private OnToolbarModificationListener mOnToolbarModificationListener;

    @Override
    public int getType() {
        return MY_OWN;
    }

    @Override
    public boolean isFloatingActionButtonVisible() {
        return false;
    }

    @Override
    public TextView getEmptyView() {return mEmptyView;}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnToolbarModificationListener = (OnToolbarModificationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToolbarElevationListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnToolbarModificationListener.setToolbarElevation(true);
        mOnToolbarModificationListener.setSortFilterIconsVisibility(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myevents, container, false);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        super.inflateWithEvents();

        mOnToolbarModificationListener.setToolbarElevation(false);
        mOnToolbarModificationListener.setSortFilterIconsVisibility(false);
        return view;
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
        mViewPager.setAdapter(new TabEventsPagerAdapter(getActivity(), new String[]{upcoming, past}, events, MY_OWN));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == INDETAIL_EVENT_REQUEST) {
            PagerAdapter adapter = mViewPager.getAdapter();
            adapter.notifyDataSetChanged();
        } else if (requestCode == CreateEventActivity.EDIT_MY_EVENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "result ok");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

                Log.d(TAG, "result canceled");
            }
        }
    }
}
