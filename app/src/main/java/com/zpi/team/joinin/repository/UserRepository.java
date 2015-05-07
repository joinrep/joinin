package com.zpi.team.joinin.repository;

import android.graphics.Color;
import android.util.Log;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MK on 2015-03-19.
 */
public class UserRepository implements IRepository<User> {

    private JSONParser jParser = new JSONParser();
    private static String url_login_user = hostname + "login_user.php";

    public static final String TAG_USER = "user";
    public static final String TAG_ORGANIZER = "organizer";
    public static final String TAG_PARTICIPANTS = "participants_array";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_FACEBOOK_ID = "facebook_id";
    public static final String TAG_GOOGLE_ID = "google_id";
    public static final String TAG_FIRST_NAME = "first_name";
    public static final String TAG_LAST_NAME = "last_name";
    private static final String TAG_SUCCESS = "success";

    public User getById(String userID) {
        User user = new User(1, null, null, "FName", "LName");
        user.setFavoriteCategories(Arrays.asList(new Category[]{new Category(1, "Piłka Nożna", "iconPath", Color.parseColor("#000000"))}));
        user.setFriends(Arrays.asList(new User[]{new User(2, null, null, "FName2", "LName2")}));
        return user;
    }

    public User loginUser(User user) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (user.getFacebookId() != null) {
            params.add(new BasicNameValuePair(TAG_FACEBOOK_ID, user.getFacebookId()));
        }
        if (user.getGoogleId() != null) {
            params.add(new BasicNameValuePair(TAG_GOOGLE_ID, user.getGoogleId()));
        }
        params.add(new BasicNameValuePair(TAG_FIRST_NAME, user.getFirstName()));
        params.add(new BasicNameValuePair(TAG_LAST_NAME, user.getLastName()));

        JSONObject json = jParser.makeHttpRequest(url_login_user, "GET", params);
        Log.d("User: ", json.toString());

        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONObject userJSON = json.getJSONObject(TAG_USER);
                int id = userJSON.getInt(TAG_USER_ID);
                String firstName = userJSON.getString(TAG_FIRST_NAME);
                String lastName = userJSON.getString(TAG_LAST_NAME);

                user.setUserId(id);
                user.setFirstName(firstName);
                user.setLastName(lastName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

}
