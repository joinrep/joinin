package com.zpi.team.joinin.ui.newevent;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.repository.exceptions.EventFullException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventFragment extends Fragment {
    private final String TAG = "CreateEventFragment";
    public final static String TITLE = "title";
    public final static String START_CALENDAR = "start_time";
    public final static String END_CALENDAR = "end_time";
    public final static String CATEGORY ="category";
    public final static String LOCALIZATION = "localization";
    public final static String DESCRIPTION = "description";
    public final static String PARTICIPATION = "participation";
    public final static String PPL_LIMIT = "ppl_limit";
    public final static String COST = "cost";

    private TextView mStartDate, mEndDate, mStartTime, mEndTime, mCounter;
    private EditText mTitle, mDescription, mAddress, mLimit, mPay;
    private Spinner mCategories;
    private Calendar mCalendarStart, mCalendarEnd;
    private SimpleDateFormat mDateFormat, mTimeFormat;
    private Switch mLimitSwitch, mPaySwitch;
    private CheckBox mParticipationBox;

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

        mCounter.setText("0" + getResources().getString(R.string.max_title_length));
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

        mAddress = (EditText) rootView.findViewById(R.id.localization);
        mDescription = (EditText) rootView.findViewById(R.id.description);
        mParticipationBox = (CheckBox) rootView.findViewById(R.id.participation_box);
        mLimit = (EditText) rootView.findViewById(R.id.limit);
        mPay = (EditText) rootView.findViewById(R.id.pay);
        mPaySwitch = (Switch) rootView.findViewById(R.id.pay_switch);
        mLimitSwitch = (Switch) rootView.findViewById(R.id.limit_switch);

        mPay.setOnFocusChangeListener(mEditTextFocusChangedListener);
        mPay.setOnKeyListener(mOnKeyDownListener);
        mLimit.setOnFocusChangeListener(mEditTextFocusChangedListener);
        mLimit.setOnKeyListener(mOnKeyDownListener);
        mPaySwitch.setOnCheckedChangeListener(mSwitchCheckedListner);
        mLimitSwitch.setOnCheckedChangeListener(mSwitchCheckedListner);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle data = getArguments();
        if(data != null){
            mTitle.setText(data.getString(TITLE));
            mCalendarStart.setTimeInMillis(data.getLong(START_CALENDAR));
            mCalendarEnd.setTimeInMillis(data.getLong(END_CALENDAR));
            mStartDate.setText(mDateFormat.format(mCalendarStart.getTime()));
            mStartTime.setText(mTimeFormat.format(mCalendarStart.getTime()));
            mEndDate.setText(mDateFormat.format(mCalendarEnd.getTime()));
            mEndTime.setText(mTimeFormat.format(mCalendarEnd.getTime()));
            mCategories.setSelection(getCategoryPosition(data.getString(CATEGORY)));
            mAddress.setText(data.getString(LOCALIZATION));
            mDescription.setText(data.getString(DESCRIPTION));
            mParticipationBox.setChecked(data.getBoolean(PARTICIPATION));
            int limit = data.getInt(PPL_LIMIT);
            if(limit != -1){
                mLimitSwitch.setChecked(true);
                mLimit.setText(Integer.toString(limit));
            }
            double cost = data.getDouble(COST);
            if(cost != 0){
                mPaySwitch.setChecked(true);
                mPay.setText(Double.toString(cost));
            }

            mTitle.requestFocus();
        }
    }

    private int getCategoryPosition(String category) {
        int position = 0;
        for (int i = 0; i < mCategories.getAdapter().getCount() ; i++) {
            String name = ((Category)mCategories.getAdapter().getItem(i)).getName();
            if(name.equals(category)){
                position = i;
                break;
            }
        }
        return position;
    }

    public boolean isFilled() {
        return mTitle.getText().toString().trim().length() != 0 &&
                !((Category) mCategories.getSelectedItem()).getName().contentEquals(getResources().getString(R.string.choose_category)) &&
                mAddress.getText().toString().trim().length() != 0 &&
                mDescription.getText().toString().trim().length() != 0 && datesAreValid();
    }

    private boolean datesAreValid() {
        return mCalendarEnd.getTimeInMillis() - mCalendarStart.getTimeInMillis() > 0;
    }

    public void highlightInputs() {
        int color = getResources().getColor(R.color.colorAccent);
        if (isEmpty(mTitle))
            mTitle.setHintTextColor(color);
        if (isEmpty(mAddress))
            mAddress.setHintTextColor(color);
        if (isEmpty(mDescription))
            mDescription.setHintTextColor(color);
        if (!datesAreValid()) {
            mEndDate.setTextColor(color);
            mEndTime.setTextColor(color);
        }
        if (((Category) mCategories.getSelectedItem()).getName().contentEquals(getResources().getString(R.string.choose_category)))
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

    EditText.OnKeyListener mOnKeyDownListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                v.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return true;
            }
            return false;
        }
    };

    public Event saveNewEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String address = mAddress.getText().toString();
        int limit = (int) parseEditText(mLimitSwitch, mLimit, -1d);
        double cost = parseEditText(mPaySwitch, mPay, 0d);

        final Event newEvent = new Event(0, title, mCalendarStart, mCalendarEnd, description, description, limit, cost, false, 0, false);

        String out = "Title: " + title + "\nStart time: " + mCalendarStart.toString() + "\nEnd time: " + mCalendarEnd.toString() +
                "\nDescription: " + description + "\nAddress: " + address + "\nLimit: " + limit + "\nCost: " + cost;
        Log.d("CreateEventFragment", "saveNewEvent(), " + out);

        newEvent.setLocation(new Address(0, "city", "street", "street", address));
        newEvent.setOrganizer(SessionStorage.getInstance().getUser());
        newEvent.setCategory((Category) mCategories.getSelectedItem());

        boolean isOrganizerParticipating = mParticipationBox.isChecked();
        new SaveNewEvent().execute(newEvent, isOrganizerParticipating);

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

    private class SaveNewEvent extends AsyncTask<Object, Void, Void> {

        protected Void doInBackground(Object... eventInfo) {
            Event event = (Event) eventInfo[0];
            new EventRepository().create(event);

            if ((boolean) eventInfo[1]) {
                String n = event.getOrganizer().getLastName();
                Log.d(TAG, "organizer is participating; " + n );

                try {
                    event.setParticipate(true);
                    event.setParticipantsCount(1);
                    new EventRepository().participate(event, event.getOrganizer());
                } catch (EventFullException e) {
                    e.printStackTrace();
                }
            }
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