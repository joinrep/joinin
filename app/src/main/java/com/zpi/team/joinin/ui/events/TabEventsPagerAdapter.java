package com.zpi.team.joinin.ui.events;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.ui.common.CustomPopupMenu;
import com.zpi.team.joinin.ui.details.InDetailEventActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-05-06.
 */
class TabEventsPagerAdapter extends PagerAdapter implements TabEventsRecyclerAdapter.OnPopUpListener {
    private final static int UPCOMING_TYPE = 0;
    private final static int HISTORY_TYPE = 1;
    private final static int INDETAIL_EVENT_REQUEST = 2;

    private Activity mActivity;
    private String[] mPageTitle;
    private int mType;
    private String[] mUpcomingMenuItems, mHistoryMenuItems;
    private List<Event> mUpcomingEvents = new ArrayList<Event>();
    private List<Event> mHistoryEvents = new ArrayList<Event>();
    private List<RecyclerView> mViews = new ArrayList<RecyclerView>();
    RecyclerView eventsList;
    TabEventsPagerAdapter(Activity activity, String[] titles, List<Event> events, int type) {
        mActivity = activity;
        mPageTitle = titles;
        mType = type;
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
        View view = mActivity.getLayoutInflater().inflate(R.layout.pager_item,
                container, false);
        container.addView(view);

        TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
        eventsList = (RecyclerView) view.findViewById(R.id.tabEventsList);
        eventsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        eventsList.setLayoutManager(layoutManager);

        final List<Event> events;
        switch (position) {
            case UPCOMING_TYPE:
                events = mUpcomingEvents;
                if(mType == EventsRecyclerFragment.MY_OWN) mUpcomingMenuItems = new String[]{"Odwołaj", "Edytuj"};
                else if(mType == EventsRecyclerFragment.PARTICIPATE) mUpcomingMenuItems = new String[]{"Zrezygnuj"};
                break;
            case HISTORY_TYPE:
                events = mHistoryEvents;
                if(mType == EventsRecyclerFragment.MY_OWN) mHistoryMenuItems = new String[]{"Usuń"};
                else if(mType == EventsRecyclerFragment.PARTICIPATE) mHistoryMenuItems = new String[]{};
                break;
            default:
                events = new ArrayList<Event>();
        }


        OnRecyclerViewClickListener itemClickListener = new OnRecyclerViewClickListener() {
            @Override
            public void onRecyclerViewItemClicked(View v, int position) {
                SessionStorage.getInstance().setEventInDetail(events.get(position));
                Intent detail = new Intent(mActivity, InDetailEventActivity.class);
                mActivity.startActivityForResult(detail, INDETAIL_EVENT_REQUEST);
            }
        };

        TabEventsRecyclerAdapter adapter = new TabEventsRecyclerAdapter(mActivity, events, itemClickListener, this);
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

    @Override
    public void showPopUp(View v) {
//        ListPopupWindow popup = new ListPopupWindow(mActivity);
//        popup.setAdapter(new ArrayAdapter<>(mActivity, R.layout.overflow_item, items));
//
//        popup.setAnchorView(v);
//        float density = mActivity.getResources().getDisplayMetrics().density;
//        float marginPixels = -40 * density;
//        float width = 112 * density;
//        popup.setWidth((int) width);
//        popup.setVerticalOffset((int) marginPixels);
//        popup.setHorizontalOffset(-68 * (int) density);
//        popup.show();
        Log.d("TabEventsPagerAdapter", "showPopUp(),: " + mPageTitle);

        CustomPopupMenu menu = new CustomPopupMenu(mActivity);

        menu.setAdapter(new ArrayAdapter<>(mActivity, R.layout.overflow_item, mUpcomingEvents));
        menu.setAnchorView(v);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String opt = (String)parent.getItemAtPosition(position);
                Log.d("TabEventsPagerAdapter", "showPopUp(),: " + opt );
            }
        });

        menu.show();

    }


}