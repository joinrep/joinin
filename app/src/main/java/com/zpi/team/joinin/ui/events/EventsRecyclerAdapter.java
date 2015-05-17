package com.zpi.team.joinin.ui.events;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-03-15.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> implements Filterable {
    private List<Event> mEvents;
    private List<Event> mFilteredEvents;
    private Context mContext;
    private OnRecyclerViewClickListener mItemListener;
    private Filter mEventFilter;
    private EventsSorter mEventsSorter;

    public EventsRecyclerAdapter(Context context, List<Event> events, OnRecyclerViewClickListener listener) {
        mEvents = events;
        mFilteredEvents = events;
        mContext = context;
        mItemListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mAddress;
        public TextView mTime;
        public TextView mDate;
        public ImageView mImage;
        public ImageView mStar;
        public Button mParticipate;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView)v.findViewById(R.id.eventName);
            mAddress = (TextView)v.findViewById(R.id.eventAddress);
            mTime = (TextView)v.findViewById(R.id.eventTime);
            mDate = (TextView)v.findViewById(R.id.eventDate);
            mImage = (ImageView)v.findViewById(R.id.eventImage);
            mStar = (ImageView)v.findViewById(R.id.imgParticipant);
            mParticipate = (Button)v.findViewById(R.id.btnParticipate);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Event event = mFilteredEvents.get(position);

        holder.mImage.setImageResource(event.getCategory().getIconId());
        holder.mImage.setBackgroundColor(event.getCategory().getColor());
        holder.mTitle.setText(event.getName());
        holder.mAddress.setText(event.getLocation().getLocationName());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String start = timeFormat.format(event.getStartTime().getTime());
        String end = timeFormat.format(event.getEndTime().getTime());
        holder.mTime.setText(start + "-" + end);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        holder.mDate.setText(dateFormat.format(event.getStartTime().getTime()));

        toggleParticipateBtn(event, holder);
        //TODO sprawdzenie czy sa wolne miejsca, jesli nie to przycisk nieaktywny/'brak miejsc'
        holder.mParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("participate", ""+event.getId() + ":" + event.getName());

                event.setParticipate(!event.getParticipate());
                User currentUser = SessionStorage.getInstance().getUser();
                if (event.getParticipate()) {
                    event.getParticipants().add(currentUser);
                    event.setParticipantsCount(event.getParticipantsCount() + 1);
                } else {
                    event.getParticipants().remove(currentUser);
                    event.setParticipantsCount(event.getParticipantsCount() - 1);
                }
                toggleParticipateBtn(event, holder);
                new ToggleParticipate(event).execute();

            }
        });

    }

    @Override
    public Filter getFilter() {
        if (mEventFilter == null) {
            mEventFilter = new EventsFilter(this, mEvents);
        }
        return mEventFilter;
    }

    public EventsSorter getSorter() {
        if (mEventsSorter == null) {
            mEventsSorter = new EventsSorter(this, mFilteredEvents);
        }
        return mEventsSorter;
    }

    public Event getItem(int id) {
        return mFilteredEvents.get(id);
    }

    public void putItem(Event event) {
        mEvents.add(event);
    }

    protected void publishFilteredEvents(List<Event> events) {
        mFilteredEvents = events;
        notifyDataSetChanged();
    }

    private void toggleParticipateBtn(Event event, ViewHolder holder) {
        if (event.getParticipate()) {
            holder.mParticipate.setText(mContext.getResources().getString(R.string.not_participate_event));
            holder.mStar.setVisibility(View.VISIBLE);
        } else {
            holder.mParticipate.setText(mContext.getResources().getString(R.string.participate_event));
            holder.mStar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredEvents.size();
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
