package com.zpi.team.joinin.repository;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Override
    public Event getById() {
        // TODO
        try{Thread.sleep(1000);} catch (InterruptedException e){};
        Event event = new Event(1,"eventName1", Calendar.getInstance(), Calendar.getInstance(), "eventDescription1", 10, 0, false);
        return event;
    }

    @Override
    public List<Event> getAll() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
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

                    Event event = new Event(id, name, start, end, "", limit, cost, false);
                    event.setCategory(new Category(categoryName, categoryIcon));
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

    @Override
    public void create(Event entity) {
        // TODO
    }

    @Override
    public void delete(Event entity) {
        // TODO
    }

    @Override
    public void update(Event entity) {
        // TODO
    }
}
