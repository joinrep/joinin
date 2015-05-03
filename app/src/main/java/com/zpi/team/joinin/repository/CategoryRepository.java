package com.zpi.team.joinin.repository;

import android.graphics.Color;
import android.util.Log;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.entities.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by MK on 2015-03-16.
 */
public class CategoryRepository implements IRepository<Category> {

    private JSONParser jParser = new JSONParser();
    private static String url_all_categories = hostname + "get_all_categories.php";
    private static String url_categories_by_user = hostname + "get_categories_by_user.php";
    private static String url_set_favorite = hostname + "set_category_favorite.php";
    private static String url_sync_favorite = hostname + "sync_category_favorite.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_ID = "categoryId";
    private static final String TAG_NAME = "name";
    private static final String TAG_ICON = "icon_path";
    private static final String TAG_COLOR = "category_color";
    private static final String TAG_ISFAV = "is_favorite";

    public Category getByName(String categoryName) {
        // TODO
        Category result =  new Category(1, "Sample category", "sample.png", Color.parseColor("#000000"));
        result.setEvents(Arrays.asList(new Event[]{
            new Event(1,"eventName1", Calendar.getInstance(),  Calendar.getInstance(), "eventDescription1", "notatka1", 10, 0, false, 0, false),
            new Event(2,"eventName2",  Calendar.getInstance(),  Calendar.getInstance(), "eventDescription2", "notatka2", 20, 10, false, 0, false)
        }));
        return result;
    }

    public List<Category> getAll() {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_all_categories, "GET", params);

        // Check your log cat for JSON response
        Log.d("All Categories: ", json.toString());

        List<Category> result = new ArrayList<Category>();

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // categories found
                // Getting Array of Categories
                JSONArray categories = json.getJSONArray(TAG_CATEGORIES);

                // looping through All Categories
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);

                    // Storing each json item in variable
                    int id = category.getInt(TAG_ID);
                    String name = category.getString(TAG_NAME);
                    String iconPath = category.getString(TAG_ICON);
                    String color = category.getString(TAG_COLOR);

                    result.add(new Category(id, name, iconPath, Color.parseColor(color)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Category> getByUser(User user) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_categories_by_user, "GET", params);

        // Check your log cat for JSON response
        Log.d("All Categories: ", json.toString());

        List<Category> result = new ArrayList<Category>();

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // categories found
                // Getting Array of Categories
                JSONArray categories = json.getJSONArray(TAG_CATEGORIES);

                // looping through All Categories
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);

                    // Storing each json item in variable
                    int id = category.getInt(TAG_ID);
                    String name = category.getString(TAG_NAME);
                    String iconPath = category.getString(TAG_ICON);
                    boolean isFav = category.getBoolean(TAG_ISFAV);
                    String color = category.getString(TAG_COLOR);

                    result.add(new Category(id, name, iconPath, Color.parseColor(color), isFav));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void setFavorite(User user, Category category) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
        params.add(new BasicNameValuePair("category_id", "" + category.getId()));
        params.add(new BasicNameValuePair("is_favorite", category.isUserFavorite()?"Y":"N"));

        JSONObject json = jParser.makeHttpRequest(url_set_favorite, "POST", params);
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

    public void syncFavorite(User user, List<Category> categories) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", "" + user.getUserId()));
        for (Category category : categories) {
            // setFavorite(user, category);
            params.add(new BasicNameValuePair("" + category.getId(), category.isUserFavorite()?"Y":"N"));
        }

        JSONObject json = jParser.makeHttpRequest(url_sync_favorite, "POST", params);
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

}
