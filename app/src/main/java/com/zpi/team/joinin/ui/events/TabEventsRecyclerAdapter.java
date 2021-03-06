package com.zpi.team.joinin.ui.events;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.repository.exceptions.EventFullException;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-05-06.
 */
public class TabEventsRecyclerAdapter extends RecyclerView.Adapter<TabEventsRecyclerAdapter.ViewHolder> {
    private final static String TAG = "TabEventsRecyclerAdapter";
    private List<Event> mEvents;
    private Context mContext;
    private OnRecyclerViewClickListener mItemListener;
    private OnPopUpListener mPopupListener;
    private boolean mWithMenu;

    public TabEventsRecyclerAdapter(Context context, List<Event> events, OnRecyclerViewClickListener listener, OnPopUpListener popupListener, boolean withMenu) {
        mEvents = events;
        mContext = context;
        mItemListener = listener;
        mPopupListener = popupListener;
        mWithMenu = withMenu;
    }

    public interface OnPopUpListener {
        public void showPopUp(View v, Event event, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;
        public TextView mDeadline;
        public TextView mParticipants;
        public TextView mDate;
        public ImageView mImage, mMore;
        public Event mEventBelonging;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.eventName);
            mDeadline = (TextView) v.findViewById(R.id.eventDeadline);
            mParticipants = (TextView) v.findViewById(R.id.eventParticipants);
            mDate = (TextView) v.findViewById(R.id.eventDate);
            mImage = (ImageView) v.findViewById(R.id.eventImage);
            mMore = (ImageView) v.findViewById(R.id.more);
            if(mWithMenu)
                mMore.setOnClickListener(this);
            else
                mMore.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.more:
                    mPopupListener.showPopUp(mMore, mEventBelonging, this.getPosition());
                    break;
                default:
                    mItemListener.onRecyclerViewItemClicked(v, this.getPosition());

            }
        }
    }

    @Override
    public TabEventsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tabevents_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

        holder.mEventBelonging = event;
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
