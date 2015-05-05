package com.zpi.team.joinin.ui.details;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Address;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.ui.newevent.CategoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InDetailEventFragment extends Fragment {
    private static String INDETAIL_EVENT_ID = "indetail_event_id";
    private TextView mTitle, mCategory, mPpl, mLimit, mPrice, mDescription, mLocalization, mStartTime, mEndTime;
    private View mLimitContent, mPriceContent;
    private ProgressBar barParticipants, barLocalization, barDescription;
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
        barParticipants = (ProgressBar) rootView.findViewById(R.id.bar_participants);
        barLocalization = (ProgressBar) rootView.findViewById(R.id.bar_localization);
        barDescription = (ProgressBar) rootView.findViewById(R.id.bar_description);
        barParticipants.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        barDescription.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        barLocalization.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);

        fillViews();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int eventId = getActivity().getIntent().getExtras().getInt(INDETAIL_EVENT_ID);
        mParticipantsCount = mInDetailEvent.getParticipantsCount();
        new AsyncTask<Void, Void, Event>() {
            @Override
            protected void onPreExecute() {
                barDescription.setVisibility(View.VISIBLE);
                barLocalization.setVisibility(View.VISIBLE);
                if (mParticipantsCount != 0)
                    barParticipants.setVisibility(View.VISIBLE);
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
                    barParticipants.setVisibility(View.GONE);
                }
                barDescription.setVisibility(View.GONE);
                barLocalization.setVisibility(View.GONE);
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

        mPpl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("onTextChanged", "" +s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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