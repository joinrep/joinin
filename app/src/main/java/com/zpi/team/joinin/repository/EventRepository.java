package com.zpi.team.joinin.repository;

import android.util.Log;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Address;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.exceptions.EventFullException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class EventRepository implements IRepository<Event> {

    private JSONParser jParser = new JSONParser();
    private static String url_all_events = hostname + "get_all_events.php";
    private static String url_event_by_id = hostname + "get_event_by_id.php";
    private static String url_create_event = hostname + "create_event.php";
    private static String url_edit_event = hostname + "edit_event.php";
    private static String url_participate_event = hostname + "participate_event.php";
    private static String url_events_by_category = hostname + "get_events_by_category.php";
    private static String url_events_by_participant = hostname + "get_events_by_participant.php";
    private static String url_events_by_organizer = hostname + "get_events_by_organizer.php";
    private static String url_cancel_event = hostname + "delete_event.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_ID = "id";
    private static final String TAG_EVENT_ID = "event_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_START = "start_time";
    private static final String TAG_END = "end_time";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LIMIT = "size_limit";
    private static final String TAG_COST = "cost";
    private static final String TAG_CANCELED = "canceled";
    private static final String TAG_PARTICIPANTS = "participants";
    private static final String TAG_CATEGORY_ID = "category_id";
    private static final String TAG_IS_PARTICIPANT = "is_participant";

    private static final String ERROR_EVENT_FULL = "Event is full";

    public Event getById(User user, int eventID) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
        params.add(new BasicNameValuePair("event_id", "" + eventID));

        JSONObject json = jParser.makeHttpRequest(url_event_by_id, "GET", params);

        Log.d("Event by id: ", json.toString());

        Event result = null;
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONObject eventJSON = json.getJSONObject(TAG_EVENT);
                result = parseSimpleEvent(eventJSON);


                JSONObject addressJSON = eventJSON.getJSONObject(AddressRepository.TAG_ADDRESS);
                String city = addressJSON.getString(AddressRepository.TAG_CITY);
                String street1 = addressJSON.getString(AddressRepository.TAG_STREET1);
                String street2 = addressJSON.getString(AddressRepository.TAG_STREET2);
                String location_name = addressJSON.getString(AddressRepository.TAG_LOCATION_NAME);
                result.setLocation(new Address(0,city,street1,street2,location_name));


                JSONObject organizerJSON = eventJSON.getJSONObject(UserRepository.TAG_ORGANIZER);
                int user_id = organizerJSON.getInt(UserRepository.TAG_USER_ID);
                String facebook_id = organizerJSON.getString(UserRepository.TAG_FACEBOOK_ID);
                String google_id = organizerJSON.getString(UserRepository.TAG_GOOGLE_ID);
                String first_name = organizerJSON.getString(UserRepository.TAG_FIRST_NAME);
                String last_name = organizerJSON.getString(UserRepository.TAG_LAST_NAME);

                if ("null".equals(facebook_id)) {
                    facebook_id = null;
                }
                if ("null".equals(google_id)) {
                    google_id = null;
                }

                result.setOrganizer(new User(user_id, facebook_id, google_id, first_name, last_name));


                JSONArray participantsJSON = eventJSON.getJSONArray(UserRepository.TAG_PARTICIPANTS);
                List<User> participants = new ArrayList<User>();
                for (int i = 0; i < participantsJSON.length(); i++) {
                    JSONObject participantJSON = participantsJSON.getJSONObject(i);
                    user_id = participantJSON.getInt(UserRepository.TAG_USER_ID);
                    facebook_id = participantJSON.getString(UserRepository.TAG_FACEBOOK_ID);
                    google_id = participantJSON.getString(UserRepository.TAG_GOOGLE_ID);
                    first_name = participantJSON.getString(UserRepository.TAG_FIRST_NAME);
                    last_name = participantJSON.getString(UserRepository.TAG_LAST_NAME);

                    if ("null".equals(facebook_id)) {
                        facebook_id = null;
                    }
                    if ("null".equals(google_id)) {
                        google_id = null;
                    }

                    participants.add(new User(user_id, facebook_id, google_id, first_name, last_name));
                }
                result.setParticipants(participants);

                String descripton = eventJSON.getString(TAG_DESCRIPTION);
                result.setDescription(descripton);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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
        boolean isParticipant = eventJSON.getBoolean(TAG_IS_PARTICIPANT);
        String locationName = eventJSON.getString(AddressRepository.TAG_LOCATION_NAME);

        Event event = new Event(id, name, start, end, "", "", limit, cost, false, participantsCount, isParticipant);
        event.setLocation(new Address(0,"","","",locationName));
        event.setCategory(SessionStorage.getInstance().getCategory(categoryId));

        return event;
    }

    public List<Event> getAll(User user) {
        return getAll(user, false);
    }

    public List<Event> getAll(User user, boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
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

    public List<Event> getByCategory(Category category, User user) {
        return getByCategory(category, user, false);
    }

    public List<Event> getByCategory(Category category, User user, boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("category_id", "" + category.getId()));
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
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

    private List<NameValuePair> getKeyValueEvent(Event entity) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_EVENT_ID, "" + entity.getId()));
        params.add(new BasicNameValuePair("event_name", entity.getName()));
        params.add(new BasicNameValuePair(TAG_START, "" + entity.getStartTime().getTimeInMillis()));
        params.add(new BasicNameValuePair(TAG_END, "" + entity.getEndTime().getTimeInMillis()));
        params.add(new BasicNameValuePair(TAG_DESCRIPTION, entity.getDescription()));
        params.add(new BasicNameValuePair("notes", entity.getNotes()));
        params.add(new BasicNameValuePair("limit", "" + entity.getLimit()));
        params.add(new BasicNameValuePair(TAG_COST, "" + entity.getCost()));
        params.add(new BasicNameValuePair("category", "" + entity.getCategory().getId()));
        params.add(new BasicNameValuePair("location", "" + entity.getLocation().getId()));
        params.add(new BasicNameValuePair(AddressRepository.TAG_CITY, entity.getLocation().getCity()));
        params.add(new BasicNameValuePair(AddressRepository.TAG_STREET1, entity.getLocation().getStreet1()));
        params.add(new BasicNameValuePair(AddressRepository.TAG_STREET2, entity.getLocation().getStreet2()));
        params.add(new BasicNameValuePair(AddressRepository.TAG_LOCATION_NAME, entity.getLocation().getLocationName()));
        return params;
    }

    public void create(Event entity) {
        List<NameValuePair> params = getKeyValueEvent(entity);
        params.add(new BasicNameValuePair("organizer", "" + entity.getOrganizer().getUserId()));

        JSONObject json = jParser.makeHttpRequest(url_create_event, "POST", params);
        // TODO catch exceptions
        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                int eventId = json.getInt(TAG_EVENT_ID);
                entity.setId(eventId);
            } else {
                // failed to create
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void edit(Event entity) {
        List<NameValuePair> params = getKeyValueEvent(entity);

        JSONObject json = jParser.makeHttpRequest(url_edit_event, "POST", params);
        Log.d("Edit event:", json.toString());

        // TODO catch exceptions
        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {

            } else {
                // failed to create
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void participate(Event event, User user) throws EventFullException {
        participate(event, user, true);
    }

    public void participate(Event event, User user, boolean participate) throws EventFullException {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("event_id", "" + event.getId()));
            params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
            params.add(new BasicNameValuePair("participate", "" + participate));
            JSONObject json = jParser.makeHttpRequest(url_participate_event, "POST", params);
            Log.d("Participate", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created
                } else {
                    // failed to create
                    String message = json.getString(TAG_MESSAGE);
                    if (ERROR_EVENT_FULL.equals(message)) {
                        throw new EventFullException();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public List<Event> getByOrganizer(User organizer) {
        return getByOrganizer(organizer, false);
    }

    public List<Event> getByOrganizer(User organizer, boolean canceled) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + organizer.getUserId()));
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));

        JSONObject json = jParser.makeHttpRequest(url_events_by_organizer, "GET", params);

        Log.d("Organizer Events: ", json.toString());

        List<Event> result = new ArrayList<Event>();

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray events = json.getJSONArray(TAG_EVENTS);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventJSON = events.getJSONObject(i);

                    Event event = parseSimpleEvent(eventJSON);
                    String description = eventJSON.getString(TAG_DESCRIPTION);
                    event.setDescription(description);

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

    public void cancel(Event event) {
        cancel(event, true);
    }

    public void cancel(Event event, boolean canceled) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("event_id", "" + event.getId()));
        params.add(new BasicNameValuePair("canceled", canceled?"Y":"N"));
        JSONObject json = jParser.makeHttpRequest(url_cancel_event, "POST", params);
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
}
