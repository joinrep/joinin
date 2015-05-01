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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.concurrent.ExecutionException;

public class InDetailEventFragment extends Fragment {
    private static String INDETAIL_EVENT_ID = "indetail_event_id";
    private TextView mTitle, mCategory, mPpl, mLimit, mPrice, mDescription, mLocalization, mStartTime, mEndTime;
    private View mLimitContent, mPriceContent;
    private ProgressBar barLocalization, barDescription;
    private Event mInDetailEvent;

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
        barLocalization = (ProgressBar) rootView.findViewById(R.id.bar_localization);
        barDescription = (ProgressBar) rootView.findViewById(R.id.bar_description);
        barDescription.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        barLocalization.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.bar_gray), PorterDuff.Mode.SRC_IN);
        mPpl.setOnClickListener(mPplDialogListener);

        fillViews();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int eventId = getActivity().getIntent().getExtras().getInt(INDETAIL_EVENT_ID);
        Log.d("InDetaiEventFragment", "onCreateView(), id: " + eventId);

        new AsyncTask<Void, Void, Event>() {

            @Override
            protected void onPreExecute() {
//                progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.loading_events), true);
                barDescription.setVisibility(View.VISIBLE);
                barLocalization.setVisibility(View.VISIBLE);

            }

            @Override
            protected Event doInBackground(Void... params) {
                return new EventRepository().getById(eventId);
            }

            @Override
            protected void onPostExecute(Event event) {
                mLocalization.setText(event.getLocation().getLocationName());
                mDescription.setText(event.getDescription());
                barDescription.setVisibility(View.GONE);
                barLocalization.setVisibility(View.GONE);
            }
        }.execute();
    }

    private void fillViews() {
        mInDetailEvent = SessionStorage.getInstance().getEventInDetail();
        mTitle.setText(mInDetailEvent.getName());
        mCategory.setText(mInDetailEvent.getCategory().getName());
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String start = format.format(mInDetailEvent.getStartTime().getTime());
        String end = format.format(mInDetailEvent.getEndTime().getTime());
        mStartTime.setText(start);
        mEndTime.setText(end);

//        mLocalization.setText(mInDetailEvent.getLocation().getLocationName());
//        mDescription.setText(mInDetailEvent.getDescription());

        int limit = mInDetailEvent.getLimit();
        if (limit != -1) setPpl(limit);
        else mLimitContent.setVisibility(View.GONE);

        double price = mInDetailEvent.getCost();
        if (price != 0) mPrice.setText(price + " " + getString(R.string.currency));
        else mPriceContent.setVisibility(View.GONE);
    }

    View.OnClickListener mPplDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String value = ((TextView) v).getText().toString();
//            if(value != getString(R.string.no_participants)){
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_participants);
            ListView list = (ListView) dialog.findViewById(R.id.dialog_participants);
            ArrayList<String> participants = new ArrayList<>();
            participants.add("Jan Kowalski");
            participants.add("Janina Kowalska");

            list.setAdapter(new DialogListAdapter(getActivity(), participants));
            dialog.show();
//            }
        }
    };

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
}