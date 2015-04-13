package com.zpi.team.joinin.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Arkadiusz on 2015-03-15.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {
    private List<Event> mEvents;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mAddress;
        public TextView mTime;
        public TextView mDate;
        public ImageView mImage;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mAddress = (TextView)v.findViewById(R.id.eventAddress);
            mTime = (TextView)v.findViewById(R.id.eventTime);
            mDate = (TextView)v.findViewById(R.id.eventDate);
            mImage = (ImageView)v.findViewById(R.id.eventImage);
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
        Event event = mEvents.get(position);

        int[] colors = mContext.getResources().getIntArray(R.array.imageBackgroundColors);
        holder.mImage.setBackgroundColor(colors[new Random().nextInt(colors.length)]);

        holder.mTitle.setText(event.getName());
//        holder.mAddress.setText(event.getLocation().getLocationName());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String start = format.format(event.getStartTime().getTime());
        String end = format.format(event.getEndTime().getTime());
        holder.mTime.setText(start + "-" + end);



    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
