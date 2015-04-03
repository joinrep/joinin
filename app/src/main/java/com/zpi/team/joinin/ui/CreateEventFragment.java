package com.zpi.team.joinin.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.CategoryRepository;
import com.zpi.team.joinin.repository.EventRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateEventFragment extends Fragment {
    private TextView mStartDate, mEndDate, mStartTime, mEndTime, mCounter;
    private EditText mTitle;
    private Spinner mCategory;
    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat, mTimeFormat;

    private OnNewEventListener mOnNewEventListener;

    public interface OnNewEventListener{
       public String onNewEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        mCounter = (TextView) rootView.findViewById(R.id.counter);
        mTitle = (EditText) rootView.findViewById(R.id.new_event_title);
        mTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) mCounter.setVisibility(View.VISIBLE);
                else mCounter.setVisibility(View.INVISIBLE);
            }
        });
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mCounter.setText(s.length() + getResources().getString(R.string.max_title_length));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}


        });

        mStartDate = (TextView) rootView.findViewById(R.id.start_date);
        mEndDate = (TextView) rootView.findViewById(R.id.end_date);
        mStartTime = (TextView) rootView.findViewById(R.id.start_time);
        mEndTime = (TextView) rootView.findViewById(R.id.end_time);

        DateAndTimeListener listener = new DateAndTimeListener();
        mStartDate.setOnClickListener(listener);
        mEndDate.setOnClickListener(listener);
        mStartTime.setOnClickListener(listener);
        mEndTime.setOnClickListener(listener);

        mCalendar = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
        String date = mDateFormat.format(mCalendar.getTime());
        mStartDate.setText(date);
        mEndDate.setText(date);

        mTimeFormat = new SimpleDateFormat("HH:mm");
        mStartTime.setText(mTimeFormat.format(mCalendar.getTime()));
        int h = mCalendar.get(Calendar.HOUR_OF_DAY)+1;
        String time = "" + h  + ":" + new SimpleDateFormat("mm").format(mCalendar.getTime());
        mEndTime.setText(time);

        /**TODO
         * pociagnac z sharedpreferences
         */
        List<Category> categories = new ArrayList<Category>();
        categories.add(new Category(1, "Piłka", null));
        categories.add(new Category(2, "Pływanie", null));
        categories.add(new Category(3, "Bieganie", null));
        mCategory = (Spinner)rootView.findViewById(R.id.categorySpinner);
        mCategory.setAdapter(new CategoryAdapter(getActivity(), R.layout.spinner, categories));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnNewEventListener = (OnNewEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewEventListener");
        }
    }

    public String onNewEvent(){
        return "hurej";
    }

    private class DateAndTimeListener implements View.OnClickListener {
        DatePickerDialog.OnDateSetListener mStartDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear, dayOfMonth);
                String date = mDateFormat.format(mCalendar.getTime());
                mStartDate.setText(date);
            }
        };

        DatePickerDialog.OnDateSetListener mEndDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear, dayOfMonth);
                String date = mDateFormat.format(mCalendar.getTime());
                mEndDate.setText(date);
            }
        };

        TimePickerDialog.OnTimeSetListener mStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                mStartTime.setText(mTimeFormat.format(mCalendar.getTime()));
            }
        };

        TimePickerDialog.OnTimeSetListener mEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                mEndTime.setText(mTimeFormat.format(mCalendar.getTime()));
            }
        };

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_date:
                    DatePickerFragment.newInstance(mStartDateListener).show(getFragmentManager(), "startdate");
                    break;
                case R.id.end_date:
                    DatePickerFragment.newInstance(mEndDateListener).show(getFragmentManager(), "enddate");
                    break;
                case R.id.start_time:
                    TimePickerFragment.newInstance(mStartTimeListener).show(getFragmentManager(), "starttime");
                    break;
                case R.id.end_time:
                    TimePickerFragment.newInstance(mEndTimeListener).show(getFragmentManager(), "endtime");
                    break;
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener onDateSetListener;

        static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener onDateSetListener) {
            DatePickerFragment pickerFragment = new DatePickerFragment();
            pickerFragment.setOnDateSetListener(onDateSetListener);
            return pickerFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        }

        private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            this.onDateSetListener = listener;
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        private TimePickerDialog.OnTimeSetListener onTimeSetListener;

        static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
            TimePickerFragment pickerFragment = new TimePickerFragment();
            pickerFragment.setOnTimeSetListener(onTimeSetListener);
            return pickerFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        private void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
            this.onTimeSetListener = listener;
        }
    }
}