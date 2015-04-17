package com.zpi.team.joinin.ui.newevent;


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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateEventFragment extends Fragment {
    private TextView mStartDate, mEndDate, mStartTime, mEndTime, mCounter;
    private EditText mTitle, mDescription, mAddress;
    private Spinner mCategories;
    private Calendar mCalendarStart, mCalendarEnd;
    private SimpleDateFormat mDateFormat, mTimeFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        mCounter = (TextView) rootView.findViewById(R.id.counter);
        mTitle = (EditText) rootView.findViewById(R.id.new_event_title);
        mTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) mCounter.setVisibility(View.VISIBLE);
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

        mCalendarStart = Calendar.getInstance();
        mCalendarEnd = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
        String date = mDateFormat.format(mCalendarStart.getTime());
        mStartDate.setText(date);
        mEndDate.setText(date);

        mTimeFormat = new SimpleDateFormat("HH:mm");
        mStartTime.setText(mTimeFormat.format(mCalendarStart.getTime()));
        int h = mCalendarEnd.get(Calendar.HOUR_OF_DAY) + 1;
        mCalendarEnd.set(Calendar.HOUR_OF_DAY, h);
        mEndTime.setText(mTimeFormat.format(mCalendarEnd.getTime()));

        mDescription = (EditText) rootView.findViewById(R.id.description);
        mAddress = (EditText) rootView.findViewById(R.id.localization);

        mCategories = (Spinner) rootView.findViewById(R.id.categorySpinner);
        mCategories.setAdapter(new CategoryAdapter(getActivity(), R.layout.spinner, SessionStorage.getInstance().getCategories()));

        return rootView;
    }

    public boolean isFilled() {
        return mTitle.getText().toString().trim().length() != 0 &&
                !((Category) mCategories.getSelectedItem()).getName().contentEquals(getResources().getString(R.string.choose_category)) &&
                mAddress.getText().toString().trim().length() != 0 &&
                mDescription.getText().toString().trim().length() != 0;
    }

    public void highlightInputs(){
        int color = getResources().getColor(R.color.colorAccent);
        if(isEmpty(mTitle))
            mTitle.setHintTextColor(color);
        if(isEmpty(mAddress))
            mAddress.setHintTextColor(color);
        if(isEmpty(mDescription))
            mDescription.setHintTextColor(color);

        ((TextView)mCategories.getSelectedView()).setTextColor(color);
    }

    private boolean isEmpty(TextView input){
        return input.getText().toString().trim().length() == 0;
    }

    public void saveNewEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String address = mAddress.getText().toString();

        final Event newEvent = new Event(0, title, mCalendarStart, mCalendarEnd, description, description, 10, 10, false);
        newEvent.setLocation(new Address(0, "city", "street", "street", address));
        newEvent.setOrganizer(new User("12", "jan", "probny"));
        newEvent.setCategory(new Category(1, "PIlka test", "paff"));

        new SaveNewEvent().execute(newEvent);
    }

    private class SaveNewEvent extends AsyncTask<Event, Void, Void> {

        protected Void doInBackground(Event... event) {
            new EventRepository().create(event[0]);
            return null;
        }
    }

    private class DateAndTimeListener implements View.OnClickListener {
        DatePickerDialog.OnDateSetListener mStartDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendarStart.set(year, monthOfYear, dayOfMonth);
                String date = mDateFormat.format(mCalendarStart.getTime());
                mStartDate.setText(date);
            }
        };

        DatePickerDialog.OnDateSetListener mEndDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendarEnd.set(year, monthOfYear, dayOfMonth);
                String date = mDateFormat.format(mCalendarEnd.getTime());
                mEndDate.setText(date);
            }
        };

        TimePickerDialog.OnTimeSetListener mStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendarStart.set(Calendar.MINUTE, minute);
                mStartTime.setText(mTimeFormat.format(mCalendarStart.getTime()));
            }
        };

        TimePickerDialog.OnTimeSetListener mEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendarEnd.set(Calendar.MINUTE, minute);
                mEndTime.setText(mTimeFormat.format(mCalendarEnd.getTime()));
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