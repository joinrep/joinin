package com.zpi.team.joinin.repository;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Address;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Comment;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class EventRepository implements IRepository<Event> {

    private JSONParser jParser = new JSONParser();
    private static String url_all_events = hostname + "get_all_events.php";
    private static String url_event_by_id = hostname + "get_event_by_id.php";
    private static String url_create_event = hostname + "create_event.php";
    private static String url_participate_event = hostname + "participate_event.php";
    private static String url_events_by_category = hostname + "get_events_by_category.php";
    private static String url_events_by_participant = hostname + "get_events_by_participant.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_START = "start_time";
    private static final String TAG_END = "end_time";
    private static final String TAG_DESCRIPTION = "icon_path";
    private static final String TAG_LIMIT = "size_limit";
    private static final String TAG_COST = "cost";
    private static final String TAG_CANCELED = "canceled";
    private static final String TAG_PARTICIPANTS = "participants";
    private static final String TAG_CATEGORY_ID = "category_id";

    public Event getById(int eventID) {
        // TODO
        try{Thread.sleep(1000);} catch (InterruptedException e){};
        Event event = new Event(1,"eventName1", Calendar.getInstance(), Calendar.getInstance(), "eventDescription1", "eventNotes", 10, 0, false, 0);
        event.setOrganizer(new User(1,"organizerFName1", "organizerLName1"));
        event.setParticipants(Arrays.asList(new User[]{new User(2,"participantFName1", "participantLName1"), new User(3,"participantFName2", "participantLName2")}));
        event.setCategory(new Category(0,"kategoria", "iconPath", Color.parseColor("#000000")));
        event.setLocation(new Address(1, "Wrocław", "Żelazna 40", "", "Hala sportowa"));

        Comment comment = new Comment(1,Calendar.getInstance(), "Komentarz1");
        Comment subcomment = new Comment(1,Calendar.getInstance(), "Komentarz1");
        comment.setAuthor(new User(2,"participantFName1", "participantLName1"));
        subcomment.setAuthor(new User(2,"participantFName1", "participantLName1"));
        subcomment.setParentComment(comment);
        comment.setChildrenComments(Arrays.asList(new Comment[]{subcomment}));
        comment.setCommentedEvent(event);
        subcomment.setCommentedEvent(event);

        return event;
    }

    private Event parseSimpleEvent(JSONObject eventJSON) throws JSONException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int id = eventJSON.getInt(TAG_ID);
        String name = eventJSON.getString(TAG_NAME);
        Calendar start = Calendar.getInstance();
        start.setTime(dateFormat.parse(eventJSON.getString(TAG_START)));
        Calendar end = Calendar.getInstance();
        end.setTime(dateFormat.parse(eventJSON.getString(TAG_END)));
        int limit = eventJSON.getInt(TAG_LIMIT);
        double cost = eventJSON.getDouble(TAG_COST);
        int categoryId = eventJSON.getInt(TAG_CATEGORY_ID);
        int participantsCount = eventJSON.getInt(TAG_PARTICIPANTS);

        Event event = new Event(id, name, start, end, "", "", limit, cost, false, participantsCount);
        event.setCategory(SessionStorage.getInstance().getCategory(categoryId));

        return event;
    }

    public List<Event> getAll() {
        return getAll(false);
    }

    public List<Event> getAll(boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));

        JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);

        Log.d("All Events: ", json.toString());

        List<Event> result = new ArrayList<Event>();

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray events = json.getJSONArray(TAG_EVENTS);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventJSON = events.getJSONObject(i);

                    Event event = parseSimpleEvent(eventJSON);

                    result.add(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Event> getByCategory(Category category) {
        return getByCategory(category, false);
    }

    public List<Event> getByCategory(Category category, boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("category_id", "" + category.getId()));
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));

        JSONObject json = jParser.makeHttpRequest(url_events_by_category, "GET", params);

        Log.d("Category Events: ", json.toString());

        List<Event> result = new ArrayList<Event>();

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray events = json.getJSONArray(TAG_EVENTS);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventJSON = events.getJSONObject(i);

                    Event event = parseSimpleEvent(eventJSON);

                    result.add(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Event> getByParticipant(User participant) {
        return getByParticipant(participant, false);
    }

    public List<Event> getByParticipant(User participant, boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + participant.getUserId()));
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));

        JSONObject json = jParser.makeHttpRequest(url_events_by_participant, "GET", params);

        Log.d("Participant Events: ", json.toString());

        List<Event> result = new ArrayList<Event>();

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray events = json.getJSONArray(TAG_EVENTS);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventJSON = events.getJSONObject(i);

                    Event event = parseSimpleEvent(eventJSON);

                    result.add(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void create(Event entity) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("event_name", entity.getName()));
        params.add(new BasicNameValuePair("start_time", "" + entity.getStartTime().getTimeInMillis()));
        params.add(new BasicNameValuePair("end_time", "" + entity.getEndTime().getTimeInMillis()));
        params.add(new BasicNameValuePair("description", entity.getDescription()));
        params.add(new BasicNameValuePair("notes", entity.getNotes()));
        params.add(new BasicNameValuePair("limit", "" + entity.getLimit()));
        params.add(new BasicNameValuePair("cost", "" + entity.getCost()));
        params.add(new BasicNameValuePair("category", "" + entity.getCategory().getId()));
        params.add(new BasicNameValuePair("location", "" + entity.getLocation().getId()));
        params.add(new BasicNameValuePair("city", entity.getLocation().getCity()));
        params.add(new BasicNameValuePair("street1", entity.getLocation().getStreet1()));
        params.add(new BasicNameValuePair("street2", entity.getLocation().getStreet2()));
        params.add(new BasicNameValuePair("location_name", entity.getLocation().getLocationName()));
        params.add(new BasicNameValuePair("organizer", "" + entity.getOrganizer().getUserId()));

        JSONObject json = jParser.makeHttpRequest(url_create_event, "POST", params);
        // TODO catch exceptions
        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // successfully created
            } else {
                // failed to create
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void participate(Event event, User user) {
        participate(event, user, true);
    }

    public void participate(Event event, User user, boolean participate) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("event_id", "" + event.getId()));
            params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
            params.add(new BasicNameValuePair("participate", "" + participate));
            JSONObject json = jParser.makeHttpRequest(url_participate_event, "POST", params);
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created
                } else {
                    // failed to create
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void delete(Event entity) {
        // TODO
    }

    public void update(Event entity) {
        // TODO
    }
}
