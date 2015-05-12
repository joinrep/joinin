package com.zpi.team.joinin.ui.events;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by Arkadiusz on 2015-04-08.
 */
public class ParticipateEventsRecyclerAdapter extends RecyclerView.Adapter<ParticipateEventsRecyclerAdapter.ViewHolder> {
    private List<Event> mEvents;
    private Context mContext;
    private OnRecyclerViewClickListener mItemListener;


    public ParticipateEventsRecyclerAdapter(Context context, List<Event> events, OnRecyclerViewClickListener listener) {
        mEvents = events;
        mContext = context;
        mItemListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mDeadline;
        public TextView mParticipants;
        public TextView mDate;
        public ImageView mImage;
        public ImageView mMore;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mDeadline = (TextView)v.findViewById(R.id.eventDeadline);
            mParticipants = (TextView)v.findViewById(R.id.eventParticipants);
            mDate = (TextView)v.findViewById(R.id.eventDate);
            mImage = (ImageView)v.findViewById(R.id.eventImage);
            mMore = (ImageView) v.findViewById(R.id.more);
        }

        @Override
        public void onClick(View v) {
            mItemListener.onRecyclerViewItemClicked(v, this.getPosition());
        }
    }

    @Override
    public ParticipateEventsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participate_events_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Event event = mEvents.get(position);

        holder.mImage.setImageResource(event.getCategory().getIconId());
        holder.mImage.setBackgroundColor(event.getCategory().getColor());
        holder.mTitle.setText(event.getName());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String start = timeFormat.format(event.getStartTime().getTime());
        holder.mDeadline.setText(start);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        holder.mDate.setText(dateFormat.format(event.getStartTime().getTime()));

        if (event.getLimit() < 0) {
            holder.mParticipants.setText(event.getParticipantsCount() + " / " + '\u221e');
        } else {
            holder.mParticipants.setText(event.getParticipantsCount() + " / " + event.getLimit());
        }

        toggleParticipateBtn(event, holder);
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO MK dropdown menu - dodać usuwanie uczestnictwa
                event.setParticipate(!event.getParticipate());
                toggleParticipateBtn(event, holder);
                new ToggleParticipate(event).execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    private void toggleParticipateBtn(Event event, ViewHolder holder) {
        if(event.getParticipate()) {
            ((View)holder.mImage.getParent()).setAlpha(1f);
        } else {
            ((View)holder.mImage.getParent()).setAlpha(0.5f);
        }
    }

    private class ToggleParticipate extends AsyncTask<String, String, String> {
        SessionStorage storage = SessionStorage.getInstance();
        private Event event;

        ToggleParticipate(Event event) {
            super();
            this.event = event;
        }

        protected String doInBackground(String... args) {
            new EventRepository().participate(event, storage.getUser(), event.getParticipate());
            return "dumb";
        }

    }
}
