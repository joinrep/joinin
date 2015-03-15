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
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView)v.findViewById(R.id.eventName);
        }
    }

    public EventsRecyclerAdapter(Context context, List<Event> events) {
        mEvents = events;
        mContext = context;
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
        holder.mTitle.setText(mEvents.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
