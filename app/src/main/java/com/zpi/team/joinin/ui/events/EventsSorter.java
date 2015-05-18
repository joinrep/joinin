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
    public final static String DEFAULT = "default";

    private EventsRecyclerAdapter mAdapter;
    private List<Event> mData;
    private String lastSort = "";

    private String[] sortingRule = new String[] {null, null};

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

    public void update(List<Event> events) {
        mData = events;
    }

    private Comparator<Event> getSortingRule(String constraint) {
        boolean negation = false;

        if (DEFAULT.equals(constraint)) {
            if (sortingRule[1] != null) {
                negation = true;
                constraint = sortingRule[1];
            } else if (sortingRule[0] != null){
                constraint = sortingRule[0];
            } else {
                return mStartDateComparator;
            }
        } else {
            if (sortingRule[0] != null) {
                if (sortingRule[0].equals(constraint)) {
                    if (sortingRule[1] != null) {
                        sortingRule[1] = null;
                    } else {
                        negation = true;
                        sortingRule[1] = constraint;
                    }
                } else {
                    sortingRule[0] = constraint;
                }
            } else {
                sortingRule[0] = constraint;
            }
        }

        Comparator<Event> comparator = null;
        switch(constraint) {
            case ALPHABETICAL:
                comparator = mAlphabeticalComparator;
                break;

            case START_DATE:
                comparator = mStartDateComparator;
                break;

            case CREATE_DATE:
                comparator = mCreateDateComparator;
                break;
        }

        if (negation) {
            comparator = Collections.reverseOrder(comparator);
        }

        return comparator;
    }

    public void sort(CharSequence constraint) {
        Collections.sort(mData, getSortingRule(constraint.toString()));
        mAdapter.notifyDataSetChanged();
    }

}
