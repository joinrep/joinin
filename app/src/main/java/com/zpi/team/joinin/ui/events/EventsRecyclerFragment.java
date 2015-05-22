package com.zpi.team.joinin.ui.events;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.repository.EventRepository;
import com.zpi.team.joinin.signin.InternetConnection;
import com.zpi.team.joinin.ui.common.AnimatedConnectionDialog;
import com.zpi.team.joinin.ui.common.OnToolbarModificationListener;
import com.zpi.team.joinin.ui.details.InDetailEventActivity;
import com.zpi.team.joinin.ui.newevent.CreateEventActivity;
import com.zpi.team.joinin.ui.newevent.CreateEventFragment;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-25.
 */

public abstract class EventsRecyclerFragment extends Fragment implements OnRecyclerViewClickListener {
    private final String TAG = "EventsRecyclerFragment";

    private static int INDETAIL_EVENT_REQUEST = 2;
    public final static int ALL = 1;
    public final static int BY_CATEGORY = 2;
    public final static int MY_OWN = 3;
    public final static int PARTICIPATE = 4;

    private RecyclerView mEventsRecycler;
    private TextView mEmptyView;
    private View mAddEventButton;

    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventsRecyclerAdapter mAdapter;
    private OnToolbarModificationListener mOnToolbarModificationListener;

    public abstract int getType();

    public boolean isFloatingActionButtonVisible() {
        return true;
    }

    public Category getCategory() {
        return null;
    }

    public TextView getEmptyView() {return mEmptyView;}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnToolbarModificationListener = (OnToolbarModificationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToolbarElevationListener");
        }
    }
    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        view = inflater.inflate(R.layout.fragment_events, container, false);
//        } else {
//            view = super.onCreateView(inflater, container, savedInstanceState);
//        }

        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mEventsRecycler = (RecyclerView) view.findViewById(R.id.eventsList);
        mEventsRecycler.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mEventsRecycler.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO jesli zmieni sie cos w bazie ofc
                Log.d("EventsFragment", "refresh()");
                mAdapter.notifyDataSetChanged();
                ;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        if (isFloatingActionButtonVisible()) addButtonTo(view);

        inflateWithEvents();

        return view;
    }

    private void addButtonTo(View v) {
        mAddEventButton = v.findViewById(R.id.add_event_button);
        if (Build.VERSION.SDK_INT >= 21) {
            mAddEventButton.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
                    outline.setOval(0, 0, diameter, diameter);
                }
            });
            mAddEventButton.setClipToOutline(true);
        }

        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                intent.putExtra("request code", CreateEventActivity.CREATE_EVENT_REQUEST);
                startActivityForResult(intent,  CreateEventActivity.CREATE_EVENT_REQUEST);
            }
        });
    }

    public void inflateWithEvents() {
        if (InternetConnection.isAvailable(getActivity())) {
            mOnToolbarModificationListener.setSortFilterIconsVisibility(true);
            new LoadEvents().execute();
            if (isFloatingActionButtonVisible())
                mAddEventButton.animate().translationYBy(-mAddEventButton.getTranslationY());
        } else {
            showNoConnectionDialog();
        }
    }

    public void showNoConnectionDialog(){
        getEmptyView().setVisibility(View.VISIBLE);
        mOnToolbarModificationListener.setSortFilterIconsVisibility(false);
        final AnimatedConnectionDialog dialog = new AnimatedConnectionDialog(getActivity());
        dialog.clear();
        if (isFloatingActionButtonVisible())
            dialog.setViewToRaise(mAddEventButton);
        dialog.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.startAnimation();
                inflateWithEvents();
            }
        });
        dialog.replayAnimation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  CreateEventActivity.CREATE_EVENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Event newEvent = SessionStorage.getInstance().getNewlyCreated();
                if (newEvent != null) mAdapter.putItem(newEvent);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == INDETAIL_EVENT_REQUEST) {
            //RecyclerView.Adapter adapter = mEventsRecycler.getAdapter();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void checkIfEmpty() {
        if (getEmptyView() != null) {
            getEmptyView().setVisibility(mEventsRecycler.getAdapter().getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onRecyclerViewItemClicked(View v, int position) {
        SessionStorage.getInstance().setEventInDetail(mAdapter.getItem(position));
        Intent detail = new Intent(getActivity(), InDetailEventActivity.class);
        startActivityForResult(detail, INDETAIL_EVENT_REQUEST);
    }

    private class LoadEvents extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        List<Event> mEvents;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.loading_events), true);
        }

        protected Void doInBackground(Void... v) {
            SessionStorage storage = SessionStorage.getInstance();
            switch (getType()) {
                case ALL:
                    mEvents = new EventRepository().getAll(storage.getUser());
                    break;
                case PARTICIPATE:
                    mEvents = new EventRepository().getByParticipant(storage.getUser());
                    break;
                case MY_OWN:
                    mEvents = new EventRepository().getByOrganizer(storage.getUser());
                    break;
                case BY_CATEGORY:
                    mEvents = new EventRepository().getByCategory(getCategory(), storage.getUser());
                    break;
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            onCustomPostExecute(mEvents);
            progressDialog.dismiss();
        }
    }

    public void onCustomPostExecute(List<Event> events) {
        mAdapter = new EventsRecyclerAdapter(getActivity(), events, EventsRecyclerFragment.this);
        mEventsRecycler.setAdapter(mAdapter);
        checkIfEmpty();
    }

    public void filter(String constraint) {
        mAdapter.getFilter().filter(constraint);
    }

    public void sort(String constraint) {
        mAdapter.getSorter().sort(constraint);
    }
}
