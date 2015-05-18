package com.zpi.team.joinin.ui.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.signin.SignInActivity;
import com.zpi.team.joinin.ui.common.CustomPopupMenu;
import com.zpi.team.joinin.ui.common.ToggleParticipate;
import com.zpi.team.joinin.ui.details.InDetailEventActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiusz on 2015-05-06.
 */
class TabEventsPagerAdapter extends PagerAdapter implements TabEventsRecyclerAdapter.OnPopUpListener {

    private final static String TAG = "TabEventsPagerAdapter";
    private final static int UPCOMING_TYPE = 0;
    private final static int HISTORY_TYPE = 1;
    private final static int INDETAIL_EVENT_REQUEST = 2;

    private Activity mActivity;
    private String[] mPageTitle;
    private int mType;
    private String[] mUpcomingMenuItems, mHistoryMenuItems;
    private List<Event> mUpcomingEvents = new ArrayList<Event>();
    private List<Event> mHistoryEvents = new ArrayList<Event>();
    private List<List<Event>> mViewList = new ArrayList<List<Event>>();
    private List<RecyclerView> mViews = new ArrayList<RecyclerView>();
    private RecyclerView eventsRecycler;

    TabEventsPagerAdapter(Activity activity, String[] titles, List<Event> events, int type) {
        mActivity = activity;
        mPageTitle = titles;
        mType = type;
        long now = System.currentTimeMillis();
        for (Event event : events) {
            if (event.getEndTime().getTimeInMillis() >= now) {
                mUpcomingEvents.add(event);
            } else {
                mHistoryEvents.add(0, event);
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.pager_item,
                container, false);
        container.addView(view);

        TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
        eventsRecycler = (RecyclerView) view.findViewById(R.id.tabEventsList);
        eventsRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        eventsRecycler.setLayoutManager(layoutManager);
        eventsRecycler.setItemAnimator(new DefaultItemAnimator());

        boolean includeMenu = false;
//        final List<Event> eventsList;
        switch (position) {
            case UPCOMING_TYPE:
                mViewList.add(mUpcomingEvents);

                includeMenu = true;
                if (mType == EventsRecyclerFragment.MY_OWN)
                    mUpcomingMenuItems = new String[]{"Odwołaj", "Edytuj"};
                else if (mType == EventsRecyclerFragment.PARTICIPATE)
                    mUpcomingMenuItems = new String[]{"Zrezygnuj"};
                break;
            case HISTORY_TYPE:
                mViewList.add(mHistoryEvents);
//                if(mType == EventsRecyclerFragment.MY_OWN) mHistoryMenuItems = new String[]{"Usuń"};
//                else if(mType == EventsRecyclerFragment.PARTICIPATE) mHistoryMenuItems = new String[]{};
                break;
            default:
                mViewList.add(new ArrayList<Event>());
        }


        OnRecyclerViewClickListener itemClickListener = new OnRecyclerViewClickListener() {
            @Override
            public void onRecyclerViewItemClicked(View v, int pos) {
                SessionStorage.getInstance().setEventInDetail(mViewList.get(position).get(pos));
                Intent detail = new Intent(mActivity, InDetailEventActivity.class);
                mActivity.startActivityForResult(detail, INDETAIL_EVENT_REQUEST);
            }
        };

        TabEventsRecyclerAdapter adapter = new TabEventsRecyclerAdapter(mActivity, mViewList.get(position), itemClickListener, this, includeMenu);
        eventsRecycler.setAdapter(adapter);
        mViews.add(eventsRecycler);

        emptyView.setVisibility(eventsRecycler.getAdapter().getItemCount() > 0 ? View.GONE : View.VISIBLE);
        eventsRecycler.setVisibility(eventsRecycler.getAdapter().getItemCount() > 0 ? View.VISIBLE : View.GONE);
        return view;
    }

    private void notifyEventRemoved(int position){
        mViewList.get(UPCOMING_TYPE).remove(position);
        mViews.get(UPCOMING_TYPE).getAdapter().notifyItemRemoved(position);


    }


    @Override
    public int getCount() {
        return mPageTitle.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitle[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (RecyclerView rv : mViews) {
            rv.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void showPopUp(View v, final Event event, int position) {
        final CustomPopupMenu menu = new CustomPopupMenu(mActivity);

        menu.setAdapter(new ArrayAdapter<>(mActivity, R.layout.overflow_item, mUpcomingMenuItems));
        menu.setAnchorView(v);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String option = (String) parent.getItemAtPosition(position);

                String leave = mActivity.getResources().getString(R.string.leave_event);
                String cancel = mActivity.getResources().getString(R.string.cancel_event);

                if (option.equals(leave)) {
                    onCreateDialog(mActivity.getResources().getString(R.string.msg_leave_event), leave, event, position).show();
                    menu.dismiss();
                } else if (option.equals(cancel)) {
                    onCreateDialog(mActivity.getResources().getString(R.string.msg_cancel_event), cancel,null, position).show();
                    menu.dismiss();
                } else if (option.equals(mActivity.getResources().getString(R.string.edit_event))) {
//                        mCallerActivity.startActivity(new Intent(mCallerActivity, SignInActivity.class));

                }
            }
        });

        menu.show();

    }

    private Dialog onCreateDialog(final String msg, String msgPositive, final Event event, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(msg)
                .setPositiveButton(msgPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (msg.equals(mActivity.getResources().getString(R.string.msg_leave_event))) {
                            event.setParticipate(!event.getParticipate());
                            event.getParticipants().remove(SessionStorage.getInstance().getUser());
                            event.setParticipantsCount(event.getParticipantsCount() - 1);
                            notifyEventRemoved(position);
                        new ToggleParticipate(mActivity).execute(event);
                        } else if (msg.equals(mActivity.getResources().getString(R.string.msg_cancel_event))) {

                            Log.d(TAG, "onCreateDialog(), odwolaj");
                        }


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

}