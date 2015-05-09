package com.zpi.team.joinin.ui.details;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;

import java.text.SimpleDateFormat;
import java.util.List;

public class InDetailEventFragment extends Fragment {
    public static String INDETAIL_EVENT_ID = "indetail_event_id";
    private TextView mTitle, mCategory, mPpl, mLimit, mPrice, mDescription, mLocalization, mStartTime, mEndTime;
    private View mLimitContent, mPriceContent;
    private ProgressBar mBarParticipants, mBarLocalization, mBarDescription;
    private Event mInDetailEvent;
    private List<User> mParticipants;
    private Button mParticipate;
    private int mParticipantsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details_event, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mCategory = (TextView) rootView.findViewById(R.id.category);
        mPpl = (TextView) rootView.findViewById(R.id.participants_toggle);
        mStartTime = (TextView) rootView.findViewById(R.id.start);
        mEndTime = (TextView) rootView.findViewById(R.id.end);
        mLimit = (TextView) rootView.findViewById(R.id.limit);
        mPrice = (TextView) rootView.findViewById(R.id.pay);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        mLocalization = (TextView) rootView.findViewById(R.id.localization);
        mLimitContent = rootView.findViewById(R.id.limit_content);
        mPriceContent = rootView.findViewById(R.id.price_content);
        mParticipate = (Button) rootView.findViewById(R.id.btnParticipate);
        mBarParticipants = (ProgressBar) rootView.findViewById(R.id.bar_participants);
        mBarLocalization = (ProgressBar) rootView.findViewById(R.id.bar_localization);
        mBarDescription = (ProgressBar) rootView.findViewById(R.id.bar_description);
        mBarParticipants.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        mBarDescription.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        mBarLocalization.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);

        fillViews();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        final int eventId = getActivity().getIntent().getExtras().getInt(INDETAIL_EVENT_ID);
        final int eventId = mInDetailEvent.getId();
        mParticipantsCount = mInDetailEvent.getParticipantsCount();
        new AsyncTask<Void, Void, Event>() {
            @Override
            protected void onPreExecute() {
                mBarDescription.setVisibility(View.VISIBLE);
                mBarLocalization.setVisibility(View.VISIBLE);
                if (mParticipantsCount != 0)
                    mBarParticipants.setVisibility(View.VISIBLE);
            }

            @Override
            protected Event doInBackground(Void... params) {
                return new EventRepository().getById(SessionStorage.getInstance().getUser(),eventId);
            }

            @Override
            protected void onPostExecute(Event event) {
                mLocalization.setText(event.getLocation().getLocationName());
                mDescription.setText(event.getDescription());
                if (mParticipantsCount != 0){
                    mParticipants = event.getParticipants();
                    updateParticipantsHeader(mParticipantsCount);
                    mBarParticipants.setVisibility(View.GONE);
                }
                mBarDescription.setVisibility(View.GONE);
                mBarLocalization.setVisibility(View.GONE);
            }
        }.execute();
    }

    private void fillViews() {
        mInDetailEvent = SessionStorage.getInstance().getEventInDetail();
        if (mInDetailEvent.getParticipantsCount() == 0) {
            mPpl.setText(getString(R.string.no_participants));
        } else {
            mPpl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_participants);
                    ListView list = (ListView) dialog.findViewById(R.id.dialog_participants);

                    list.setAdapter(new DialogListAdapter(getActivity(), mParticipants));
                    dialog.show();
                }
            });
        }

        mTitle.setText(mInDetailEvent.getName());
        mCategory.setText(mInDetailEvent.getCategory().getName());
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String start = format.format(mInDetailEvent.getStartTime().getTime());
        String end = format.format(mInDetailEvent.getEndTime().getTime());
        mStartTime.setText(start);
        mEndTime.setText(end);

        int limit = mInDetailEvent.getLimit();
        if (limit != -1) setPpl(limit);
        else mLimitContent.setVisibility(View.GONE);

        double price = mInDetailEvent.getCost();
        if (price != 0) mPrice.setText(price + " " + getString(R.string.currency));
        else mPriceContent.setVisibility(View.GONE);


        if (mInDetailEvent.getStartTime().getTimeInMillis() > System.currentTimeMillis()) {
            ((View)mParticipate.getParent()).setVisibility(View.INVISIBLE);
        } else {
            toggleParticipateBtn(mInDetailEvent, false);
            mParticipate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInDetailEvent.setParticipate(!mInDetailEvent.getParticipate());
                    toggleParticipateBtn(mInDetailEvent, true);
                    new ToggleParticipate(mInDetailEvent).execute();
                }
            });
        }
    }


    private void setPpl(int number) {
        String ppl = "" + number;
        if (number == 1)
            ppl += " " + getString(R.string.ppl1);
        else if (number < 4)
            ppl += " " + getString(R.string.ppl2);
        else
            ppl += " " + getString(R.string.ppl3);

        mLimit.setText(ppl);
    }

    private void toggleParticipateBtn(Event event, boolean update) {
        if (event.getParticipate()) {
            mParticipate.setText(getResources().getString(R.string.not_participate_event));
            if(update) updateParticipantsHeader(++mParticipantsCount);
        } else {
            mParticipate.setText(getResources().getString(R.string.participate_event));
            if(update) updateParticipantsHeader(--mParticipantsCount);
        }
    }

    private void updateParticipantsHeader(int count) {
        Log.d("updateParticipants", mParticipantsCount + ", " + count);
        String postscript;
        if(count == 0){
            mPpl.setText(getResources().getString(R.string.no_participants));
            mPpl.setClickable(false);
            mPpl.setTextColor(getResources().getColor(R.color.black_87));
        }else {
            if (count == 1) postscript = getResources().getString(R.string.participant);
            else postscript = getResources().getString(R.string.participants);

            mPpl.setText(count + " " + postscript);
            mPpl.setClickable(true);
            mPpl.setTextColor(getResources().getColor(R.color.colorPrimary));
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