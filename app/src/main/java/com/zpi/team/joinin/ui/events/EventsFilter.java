package com.zpi.team.joinin.ui.events;

import android.util.Log;
import android.widget.Filter;

import com.zpi.team.joinin.entities.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MK on 2015-05-15.
 */
public class EventsFilter extends Filter {

    EventsRecyclerAdapter mAdapter;
    List<Event> mData;
    List<Event> mFilteredData;

    public final static String HIDE_FULL_ONLY_FREE = "hide_full_only_free";
    public final static String HIDE_FULL = "hide_full";
    public final static String ONLY_FREE = "only_free";
    public final static String SHOW_ALL = "show_all";

    public EventsFilter(EventsRecyclerAdapter adapter, List<Event> data) {
        super();
        mAdapter = adapter;
        mData = data;
        mFilteredData = new ArrayList<Event>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        mFilteredData.clear();
        FilterResults filterResult = new FilterResults();
        switch (constraint.toString()) {
            case HIDE_FULL:
                for (Event event : mData) {
                    if (event.getLimit() == -1 || event.getLimit() - event.getParticipantsCount() > 0) {
                        mFilteredData.add(event);
                    }
                }
                break;

            case ONLY_FREE:
                for (Event event : mData) {
                    if (event.getCost() > 0) {
                        mFilteredData.add(event);
                    }
                }
                break;
            case HIDE_FULL_ONLY_FREE:
                for (Event event : mData) {
                    if ((event.getLimit() == -1 || event.getLimit() - event.getParticipantsCount() > 0) && event.getCost() > 0) {
                        mFilteredData.add(event);
                    }
                }
                break;

            case SHOW_ALL:
                mFilteredData.addAll(mData);
                break;
        }

        filterResult.values = mFilteredData;
        filterResult.count = mFilteredData.size();
        return filterResult;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if(results.values != null) {
            Log.e("Values: ", results.values.toString());
        }
        mAdapter.publishFilteredEvents((ArrayList<Event>) results.values);
    }
}