package com.zpi.team.joinin.ui.events;

import com.zpi.team.joinin.entities.Event;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by MK on 2015-05-15.
 */
public class EventsSorter {

    public final static String ALPHABETICAL = "alphabetical";
    public final static String CREATE_DATE = "create_date";
    public final static String START_DATE = "start_date";

    private EventsRecyclerAdapter mAdapter;
    private List<Event> mData;
    private String lastSort = "";

    private static Comparator<Event> mAlphabeticalComparator = new Comparator<Event>() {
        private Collator collator = Collator.getInstance(new Locale("pl", "PL"));

        @Override
        public int compare(Event left, Event right) {
            return collator.compare(left.getName(), right.getName());
        }
    };

    private static Comparator<Event> mStartDateComparator = new Comparator<Event>() {
        @Override
        public int compare(Event left, Event right) {
            return left.getStartTime().compareTo(right.getStartTime());
        }
    };

    private static Comparator<Event> mCreateDateComparator = new Comparator<Event>() {
        @Override
        public int compare(Event left, Event right) {
            return left.getId() - right.getId();
        }
    };


    EventsSorter(EventsRecyclerAdapter adapter, List<Event> events) {
        mAdapter = adapter;
        mData = events;
    }

    public void sort(CharSequence constraint) {
        String currentSort = constraint.toString();
        switch(constraint.toString()) {
            case ALPHABETICAL:
                if (lastSort.equals(currentSort)) {
                    Collections.sort(mData, Collections.reverseOrder(mAlphabeticalComparator));
                    lastSort = "";
                } else {
                    Collections.sort(mData, mAlphabeticalComparator);
                    lastSort = currentSort;
                }
                break;

            case START_DATE:
                if (lastSort.equals(currentSort)) {
                    Collections.sort(mData, Collections.reverseOrder(mStartDateComparator));
                    lastSort = "";
                } else {
                    Collections.sort(mData, mStartDateComparator);
                    lastSort = currentSort;
                }
                break;

            case CREATE_DATE:
                if (lastSort.equals(currentSort)) {
                    Collections.sort(mData, Collections.reverseOrder(mCreateDateComparator));
                    lastSort = "";
                } else {
                    Collections.sort(mData, mCreateDateComparator);
                    lastSort = currentSort;
                }
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
