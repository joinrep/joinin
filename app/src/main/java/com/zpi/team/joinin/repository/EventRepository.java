package com.zpi.team.joinin.repository;

import android.content.Intent;

import com.zpi.team.joinin.database.JSONParser;
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
    private static String url_events_by_catgory = hostname + "get_events_by_catgory.php";
    private static String url_create_event = hostname + "create_event.php";
    private static String url_participate_event = hostname + "participate_event.php";

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
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_CATEGORY_ICON = "category_icon";

    public Event getById(int eventID) {
        // TODO
        try{Thread.sleep(1000);} catch (InterruptedException e){};
        Event event = new Event(1,"eventName1", Calendar.getInstance(), Calendar.getInstance(), "eventDescription1", "eventNotes", 10, 0, false);
        event.setOrganizer(new User("1","organizerFName1", "organizerLName1"));
        event.setParticipants(Arrays.asList(new User[]{new User("2","participantFName1", "participantLName1"), new User("3","participantFName2", "participantLName2")}));
        event.setCategory(new Category(0,"kategoria", "iconPath"));
        event.setLocation(new Address(1, "Wrocław", "Żelazna 40", "", "Hala sportowa"));

        Comment comment = new Comment(1,Calendar.getInstance(), "Komentarz1");
        Comment subcomment = new Comment(1,Calendar.getInstance(), "Komentarz1");
        comment.setAuthor(new User("2","participantFName1", "participantLName1"));
        subcomment.setAuthor(new User("2","participantFName1", "participantLName1"));
        subcomment.setParentComment(comment);
        comment.setChildrenComments(Arrays.asList(new Comment[]{subcomment}));
        comment.setCommentedEvent(event);
        subcomment.setCommentedEvent(event);

        return event;
    }

    public List<Event> getAll() {
        return getAll(false);
    }

    public List<Event> getAll(boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);

        List<Event> result = new ArrayList<Event>();

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray events = json.getJSONArray(TAG_EVENTS);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventJSON = events.getJSONObject(i);

                    int id = eventJSON.getInt(TAG_ID);
                    String name = eventJSON.getString(TAG_NAME);
                    Calendar start = Calendar.getInstance();
                    start.setTime(dateFormat.parse(eventJSON.getString(TAG_START)));
                    Calendar end = Calendar.getInstance();
                    end.setTime(dateFormat.parse(eventJSON.getString(TAG_END)));
                    int limit = eventJSON.getInt(TAG_LIMIT);
                    double cost = eventJSON.getDouble(TAG_COST);
                    String categoryName = eventJSON.getString(TAG_CATEGORY_NAME);
                    String categoryIcon = eventJSON.getString(TAG_CATEGORY_ICON);

                    Event event = new Event(id, name, start, end, "", "", limit, cost, false);
                    // TODO ctaegory id from response
                    event.setCategory(new Category(0, categoryName, categoryIcon));
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
        // TODO
        entity = new Event(0,"eventName", Calendar.getInstance(), Calendar.getInstance(), "eventDescription", "eventNotes", 10, 0, false);
        entity.setCategory(new Category(1, "", ""));
        entity.setLocation(new Address(1, "", "", "", ""));
        entity.setOrganizer(new User("1", "", ""));

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
        params.add(new BasicNameValuePair("organizer", entity.getOrganizer().getFacebookId()));

        JSONObject json = jParser.makeHttpRequest(url_create_event, "POST", params);
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
            params.add(new BasicNameValuePair("user_id", user.getFacebookId()));
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
