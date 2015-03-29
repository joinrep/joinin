package com.zpi.team.joinin.ui;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zpi.team.joinin.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CreateEventFragment extends Fragment {
    private TextView mStartDate, mEndDate, mStartTime, mEndTime;
    private Calendar mCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

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
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMM yyyy");
        String date = format.format(mCalendar.getTime());
        mStartDate.setText(date);
        mEndDate.setText(date);
        return rootView;
    }

    private class DateAndTimeListener implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
        @Override
        public void onClick(View v) {
            DialogFragment datePicker = new DatePickerFragment();
            DialogFragment timePicker = new TimePickerFragment();


            switch (v.getId()) {
                case R.id.start_date:
                    DatePickerFragment.newInstance(this).show(getFragmentManager(), "datePicker");

                    break;
                case R.id.end_date:
                    datePicker.show(getFragmentManager(), "datePicker");
                    break;
                case R.id.start_time:
                    timePicker.show(getFragmentManager(), "timePicker");
                    break;
                case R.id.end_time:
                    timePicker.show(getFragmentManager(), "timePicker");
                    break;
            }
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("no elo", "" + dayOfMonth);
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

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        }
    }
}
