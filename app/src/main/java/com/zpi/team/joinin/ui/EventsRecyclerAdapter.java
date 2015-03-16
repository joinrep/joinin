package com.zpi.team.joinin.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-15.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {
    private List<Event> mEvents;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mAddress;
        public TextView mTime;
        public TextView mDate;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mAddress = (TextView)v.findViewById(R.id.eventAddress);
            mTime = (TextView)v.findViewById(R.id.eventTime);
            mDate = (TextView)v.findViewById(R.id.eventDate);
        }
    }

    public EventsRecyclerAdapter(List<Event> events) {
        mEvents = events;
    }

    @Override
    public EventsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.mTitle.setText(event.getName());
//        holder.mAddress.setText(event.getLocation().getLocationName());

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
