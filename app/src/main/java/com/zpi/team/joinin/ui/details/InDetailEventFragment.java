package com.zpi.team.joinin.ui.details;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.ui.common.LoadProfilePhoto;
import com.zpi.team.joinin.ui.common.ToggleParticipate;
import com.zpi.team.joinin.repository.exceptions.EventFullException;

import java.text.SimpleDateFormat;
import java.util.List;

public class InDetailEventFragment extends Fragment {
    private final String TAG = "InDetailEventFragment";
    private TextView mTitle, mCategory, mPpl, mLimit, mPrice, mDescription, mLocalization, mStartTime, mEndTime;
    private View mLimitContent, mPriceContent;
    private ProgressBar mBarParticipants, mBarLocalization, mBarDescription;
    private Event mInDetailEvent;
    private User mOrganizer;
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
                //TODO null poki co
                mOrganizer = event.getOrganizer();
                mLocalization.setText(event.getLocation().getLocationName());
                mDescription.setText(event.getDescription());
                mParticipants = event.getParticipants();
                if (mParticipantsCount != 0){
                    updateParticipantsHeader();
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
            mPpl.setEnabled(false);
        }

        View.OnClickListener participantsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_participants);
                ListView list = (ListView) dialog.findViewById(R.id.dialog_participants);
                Log.d(TAG, "onClick(), "+ mParticipants.size());
                list.setAdapter(new DialogListAdapter(getActivity(), mParticipants));
                dialog.show();
            }
        };

        mPpl.setOnClickListener(participantsClickListener);

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


        if (mInDetailEvent.getStartTime().getTimeInMillis() < System.currentTimeMillis()) {
            ((View)mParticipate.getParent()).setVisibility(View.GONE);
        } else {
            toggleParticipateBtn(mInDetailEvent, false);
            mParticipate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInDetailEvent.setParticipate(!mInDetailEvent.getParticipate());
                    User currentUser = SessionStorage.getInstance().getUser();
                    if (mInDetailEvent.getParticipate()) {
                        mParticipants.add(currentUser);
                        mInDetailEvent.setParticipantsCount(mInDetailEvent.getParticipantsCount() + 1);
                    } else {
                        mParticipants.remove(currentUser);
                        mInDetailEvent.setParticipantsCount(mInDetailEvent.getParticipantsCount() - 1);
                    }
                    toggleParticipateBtn(mInDetailEvent, true);
                    new ToggleParticipate(getActivity()).execute(mInDetailEvent);
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
        ((View)mParticipate.getParent()).setVisibility(View.VISIBLE);
        if (event.getParticipate()) {
            mParticipate.setText(getResources().getString(R.string.not_participate_event));
            if(update) {
                ++mParticipantsCount;
                updateParticipantsHeader();
            }
        } else {
            if (event.getLimit() - mParticipantsCount > 0) {
                mParticipate.setText(getResources().getString(R.string.participate_event));
                if(update) {
                    --mParticipantsCount;
                    updateParticipantsHeader();
                }
            } else {
                ((View)mParticipate.getParent()).setVisibility(View.GONE);
            }
        }
    }

    private void updateParticipantsHeader() {
        Log.d("updateParticipants", mParticipantsCount + "");
        String postscript;
        if(mParticipantsCount == 0) {
            mPpl.setText(getResources().getString(R.string.no_participants));
            mPpl.setEnabled(false);
            mPpl.setTextColor(getResources().getColor(R.color.black_87));
        } else {
            if (mParticipantsCount == 1) postscript = getResources().getString(R.string.participant);
            else postscript = getResources().getString(R.string.participants);

            mPpl.setText(mParticipantsCount + " " + postscript);
            mPpl.setEnabled(true);
            mPpl.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void showOrganizerInfo() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_organizer_info);
        dialog.setTitle(getResources().getString(R.string.organizer));
        inflate(dialog);
        dialog.show();

    }


    private void inflate(Dialog view){
        final ProgressBar bar = (ProgressBar) view.findViewById(R.id.bar_photo);
        bar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        final ImageView photo = (ImageView) view.findViewById(R.id.photo);
        TextView name = (TextView) view.findViewById(R.id.organizer_name);
        TextView surname = (TextView) view.findViewById(R.id.organizer_surname);

        //TODO pociagnac organizatora
        mOrganizer = mInDetailEvent.getOrganizer();
        String source = mOrganizer.getSource();
        String id = mOrganizer.getLoginId();

        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                bar.setVisibility(View.VISIBLE);
                photo.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(String... params) {
                new LoadProfilePhoto(photo, getActivity()).execute(params[0], params[1]);
                return  null;
            }

            @Override
            protected void onPostExecute(Void v) {
                bar.setVisibility(View.GONE);
                photo.setVisibility(View.VISIBLE);
            }
        }.execute(source, id);

        name.setText(mOrganizer.getFirstName());
        surname.setText(mOrganizer.getLastName());
    }
}