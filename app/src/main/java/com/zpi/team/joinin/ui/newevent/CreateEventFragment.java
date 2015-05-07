package com.zpi.team.joinin.ui.newevent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventFragment extends Fragment {
    private TextView mStartDate, mEndDate, mStartTime, mEndTime, mCounter;
    private EditText mTitle, mDescription, mAddress, mLimit, mPay;
    private Spinner mCategories;
    private Calendar mCalendarStart, mCalendarEnd;
    private SimpleDateFormat mDateFormat, mTimeFormat;
    private Switch mLimitSwitch, mPaySwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
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

        mCategories = (Spinner) rootView.findViewById(R.id.categorySpinner);
        mCategories.setAdapter(new CategoryAdapter(getActivity(), R.layout.spinner, SessionStorage.getInstance().getCategories()));

        mDescription = (EditText) rootView.findViewById(R.id.description);
        mAddress = (EditText) rootView.findViewById(R.id.localization);
        mLimit = (EditText) rootView.findViewById(R.id.limit);
        mPay = (EditText) rootView.findViewById(R.id.pay);
        mPaySwitch = (Switch) rootView.findViewById(R.id.pay_switch);
        mLimitSwitch = (Switch) rootView.findViewById(R.id.limit_switch);

        mPay.setOnFocusChangeListener(mEditTextFocusChangedListener);
        mLimit.setOnFocusChangeListener(mEditTextFocusChangedListener);
        mPaySwitch.setOnCheckedChangeListener(mSwitchCheckedListner);
        mLimitSwitch.setOnCheckedChangeListener(mSwitchCheckedListner);

        //TODO nie pokazuje sie zł przy akceptacji \/

        return rootView;
    }

    // TODO data pocz < data kon
    public boolean isFilled() {
        return mTitle.getText().toString().trim().length() != 0 &&
                !((Category) mCategories.getSelectedItem()).getName().contentEquals(getResources().getString(R.string.choose_category)) &&
                mAddress.getText().toString().trim().length() != 0 &&
                mDescription.getText().toString().trim().length() != 0;
    }

    public void highlightInputs() {
        int color = getResources().getColor(R.color.colorAccent);
        if (isEmpty(mTitle))
            mTitle.setHintTextColor(color);
        if (isEmpty(mAddress))
            mAddress.setHintTextColor(color);
        if (isEmpty(mDescription))
            mDescription.setHintTextColor(color);

        ((TextView) mCategories.getSelectedView()).setTextColor(color);
    }

    private boolean isEmpty(TextView input) {
        return input.getText().toString().trim().length() == 0;
    }

    View.OnFocusChangeListener mEditTextFocusChangedListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText = (EditText) v;
            int id = editText.getId();
            Switch theSwitch;
            String defaultText;

            if (id == R.id.limit) {
                theSwitch = mLimitSwitch;
                defaultText = getResources().getString(R.string.limit);

                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                theSwitch = mPaySwitch;
                defaultText = getResources().getString(R.string.pay);

                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

            if (!hasFocus) {
                if (isEmpty(editText))
                    theSwitch.setChecked(false);
                else
                    addPostscript(editText);
            } else {
                String text = editText.getText().toString();
                if (!text.contentEquals(defaultText)) {
                    String number = (String) text.subSequence(0, text.indexOf(" "));
                    editText.setText(number);
                } else
                    editText.setText(null);
            }
        }

        private boolean isEmpty(EditText et) {
            return et.getText().toString().isEmpty();
        }

        private void addPostscript(EditText et) {
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            String postscript;
            String value;
            if (et.getId() == R.id.limit) {
                int number = Integer.parseInt(et.getText().toString());
                value = String.valueOf(number);
                if (number == 1)
                    postscript = " " + getString(R.string.ppl1);
                else if (number < 4)
                    postscript = " " + getString(R.string.ppl2);
                else
                    postscript = " " + getString(R.string.ppl3);
            } else {
                value = et.getText().toString();
                postscript = " " + getString(R.string.currency);
            }
            et.setText(value + postscript);
        }
    };

    CompoundButton.OnCheckedChangeListener mSwitchCheckedListner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.pay_switch)
                if (isChecked) focusOn(mPay);
                else disableEditText(mPay);
            else if (buttonView.getId() == R.id.limit_switch)
                if (isChecked) focusOn(mLimit);
                else disableEditText(mLimit);
        }

        private void focusOn(EditText editText) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            editText.setText(null);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        private void disableEditText(EditText editText) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.clearFocus();
            if (editText.getId() == R.id.pay)
                editText.setText(getResources().getString(R.string.pay));
            else if (editText.getId() == R.id.limit)
                editText.setText(getResources().getString(R.string.limit));
        }
    };

    public Event saveNewEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String address = mAddress.getText().toString();
        int limit = (int) parseEditText(mLimitSwitch, mLimit, -1d);
        double cost = parseEditText(mPaySwitch, mPay, 0d);

        // TODO: MK czy organizator jest też uczestnikiem? nie wyświetlać jego eventów? czy odłączenie będzie jednoznaczne ze zniszczeniem eventu?
        final Event newEvent = new Event(0, title, mCalendarStart, mCalendarEnd, description, description, limit, cost, false, 0, false);

        String out = "Title: " + title + "\nStart time: " + mCalendarStart.toString() + "\nEnd time: " + mCalendarEnd.toString() +
                "\nDescription: " + description + "\nAddress: " + address + "\nLimit: " + limit + "\nCost: " + cost;
        Log.d("CreateEventFragment", "saveNewEvent(), " + out);
        /**
         * TODO
         * - adres poki co tylko jako string, przydalby sie konstruktor
         * - przypisanie uzytkownika
         */
        newEvent.setLocation(new Address(0, "city", "street", "street", address));
//        newEvent.setOrganizer(SessionStorage.getInstance().getUser());
        newEvent.setOrganizer(SessionStorage.getInstance().getUser());
        newEvent.setCategory((Category) mCategories.getSelectedItem());
        new SaveNewEvent().execute(newEvent);

        return newEvent;
    }

    private double parseEditText(Switch theSwitch, EditText et, double defaultValue) {
        if (theSwitch.isChecked()) {
            String text = et.getText().toString();
            if (text.contains(" "))
                defaultValue = Double.parseDouble((String) text.subSequence(0, text.indexOf(" ")));
            else
                defaultValue = Double.parseDouble(text);
        }

        return defaultValue;
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