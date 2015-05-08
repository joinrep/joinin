package com.zpi.team.joinin.ui.events;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Adapter;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.ui.common.OnToolbarElevationListener;
import com.zpi.team.joinin.ui.details.InDetailEventActivity;
import com.zpi.team.joinin.ui.details.InDetailEventFragment;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-08.
 */
public class ParticipateEventsFragment extends EventsRecyclerFragment {
    private static int INDETAIL_EVENT_REQUEST = 2;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private OnToolbarElevationListener mOnToolbarElevationListener;

    @Override
    public int getType() {
        return PARTICIPATE;
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
        return inflater.inflate(R.layout.fragment_participate_events, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (com.zpi.team.joinin.ui.events.SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onCustomPostExecute(List<Event> events) {
        String upcoming = getResources().getString(R.string.upcoming);
        String past = getResources().getString(R.string.past);
        mViewPager.setAdapter(new ParticipateEventsPagerAdapter(new String[]{upcoming, past}, events));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INDETAIL_EVENT_REQUEST) {
            PagerAdapter adapter = mViewPager.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    class ParticipateEventsPagerAdapter extends PagerAdapter {
        private String[] mPageTitle;
        private List<Event> mUpcomingEvents = new ArrayList<Event>();
        private List<Event> mHistoryEvents = new ArrayList<Event>();
        private List<RecyclerView> mViews = new ArrayList<RecyclerView>();

        ParticipateEventsPagerAdapter(String[] titles, List<Event> events) {
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
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);

            TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            RecyclerView eventsList = (RecyclerView) view.findViewById(R.id.tabEventsList);
            eventsList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            eventsList.setLayoutManager(layoutManager);

            final List<Event> events;
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


            OnRecyclerViewClickListener itemClickListener = new OnRecyclerViewClickListener() {
                @Override
                public void onRecyclerViewItemClicked(View v, int position) {
                    int id = events.get(position).getId();
                    SessionStorage.getInstance().setEventInDetail(events.get(position));
                    Intent detail = new Intent(getActivity(), InDetailEventActivity.class);
                    detail.putExtra(InDetailEventFragment.INDETAIL_EVENT_ID, id);
                    startActivityForResult(detail, INDETAIL_EVENT_REQUEST);
                }
            };

            ParticipateEventsRecyclerAdapter adapter = new ParticipateEventsRecyclerAdapter(getActivity(), events, itemClickListener);
            eventsList.setAdapter(adapter);
            mViews.add(eventsList);

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

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            for (RecyclerView rv : mViews) {
                rv.getAdapter().notifyDataSetChanged();
            }
        }

    }
}
