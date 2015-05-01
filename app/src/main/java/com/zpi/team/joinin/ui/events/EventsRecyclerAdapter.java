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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Arkadiusz on 2015-03-15.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {
    private List<Event> mEvents;
    private Context mContext;
    private static OnRecyclerViewClickListener mItemListener;

    public EventsRecyclerAdapter(Context context, List<Event> events, OnRecyclerViewClickListener listener) {
        mEvents = events;
        mContext = context;
        mItemListener = listener;
    }

    public interface OnRecyclerViewClickListener {
        public void onRecyclerViewItemClicked(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mAddress;
        public TextView mTime;
        public TextView mDate;
        public ImageView mImage;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mAddress = (TextView)v.findViewById(R.id.eventAddress);
            mTime = (TextView)v.findViewById(R.id.eventTime);
            mDate = (TextView)v.findViewById(R.id.eventDate);
            mImage = (ImageView)v.findViewById(R.id.eventImage);
        }

        @Override
        public void onClick(View v) {
            mItemListener.onRecyclerViewItemClicked(v, this.getPosition());
        }
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

        holder.mImage.setImageResource(event.getCategory().getIconId());
//        holder.mImage.setBackgroundColor(event.getCategory().getColor());
        int[] colors = mContext.getResources().getIntArray(R.array.imageBackgroundColors);
        holder.mImage.setBackgroundColor(colors[new Random().nextInt(colors.length)]);
        holder.mTitle.setText(event.getName());
// TODO       holder.mAddress.setText(event.getLocation().getLocationName());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String start = timeFormat.format(event.getStartTime().getTime());
        String end = timeFormat.format(event.getEndTime().getTime());
        holder.mTime.setText(start + "-" + end);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        holder.mDate.setText(dateFormat.format(event.getStartTime().getTime()));

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
