package com.zpi.team.joinin.ui.events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;

import java.util.List;
import java.util.Random;

/**
 * Created by Arkadiusz on 2015-04-08.
 */
public class MyOwnEventsRecyclerAdapter extends RecyclerView.Adapter<MyOwnEventsRecyclerAdapter.ViewHolder> {
    private List<Event> mEvents;
    private Context mContext;


    public MyOwnEventsRecyclerAdapter(Context context, List<Event> events) {
        mEvents = events;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mDeadline;
        public TextView mParticipants;
        public TextView mDate;
        public ImageView mImage;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mDeadline = (TextView)v.findViewById(R.id.eventDeadline);
            mParticipants = (TextView)v.findViewById(R.id.eventParticipants);
            mDate = (TextView)v.findViewById(R.id.eventDate);
            mImage = (ImageView)v.findViewById(R.id.eventImage);
        }
    }

    @Override
    public MyOwnEventsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myevents_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEvents.get(position);

        int[] colors = mContext.getResources().getIntArray(R.array.imageBackgroundColors);
        holder.mImage.setBackgroundColor(colors[new Random().nextInt(colors.length)]);

        holder.mTitle.setText(event.getName());

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
