package com.zpi.team.joinin.ui.details;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
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
import java.util.Calendar;

public class InDetailEventFragment extends Fragment {
    private TextView mTitle, mCategory, mLimit, mPrice, mDescription, mLocalization, mStartTime, mEndTime;
    private View mLimitContent, mPriceContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details_event, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mCategory = (TextView) rootView.findViewById(R.id.dCategory);
        mStartTime = (TextView) rootView.findViewById(R.id.start);
        mEndTime = (TextView) rootView.findViewById(R.id.end);
        mLimit = (TextView) rootView.findViewById(R.id.limit);
        mPrice = (TextView) rootView.findViewById(R.id.pay);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        mLocalization = (TextView) rootView.findViewById(R.id.localization);
        mLimitContent = rootView.findViewById(R.id.limit_content);
        mPriceContent = rootView.findViewById(R.id.price_content);

        Event event = SessionStorage.getInstance().getEventInDetail();
        mTitle.setText(event.getName());
        mCategory.setText(event.getCategory().getName());

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String start = format.format(event.getStartTime().getTime());
        String end = format.format(event.getEndTime().getTime());
        mStartTime.setText(start);
        mEndTime.setText(end);

// TODO       mLocalization.setText(event.getLocation().getLocationName());
        mDescription.setText(event.getDescription());

        int limit = event.getLimit();
        if(limit != -1) setPpl(limit);
        else mLimitContent.setVisibility(View.GONE);

        double price = event.getCost();
        if(price != 0) mPrice.setText(price + " " + getString(R.string.currency));
        else mPriceContent.setVisibility(View.GONE);

//        mCalendarStart = Calendar.getInstance();
//        mCalendarEnd = Calendar.getInstance();
//        mDateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
//        String date = mDateFormat.format(mCalendarStart.getTime());
//        mStartDate.setText(date);
//        mEndDate.setText(date);



        return rootView;
    }

    private void setPpl(int number){
        String ppl = "" + number ;
        if (number == 1)
            ppl += " " + getString(R.string.ppl1);
        else if (number < 4)
            ppl += " " + getString(R.string.ppl2);
        else
            ppl += " " + getString(R.string.ppl3);

        mLimit.setText(ppl);
    }
}