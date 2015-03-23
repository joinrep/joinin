package com.zpi.team.joinin.repository;

import android.util.Log;

import com.zpi.team.joinin.database.JSONParser;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.Event;

import org.apache.http.NameValuePair;
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

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_NAME = "name";
    private static final String TAG_ICON = "icon_path";

    public Category getByName(String categoryName) {
        // TODO
        Category result =  new Category("Sample category", "sample.png");
        result.setEvents(Arrays.asList(new Event[]{
            new Event(1,"eventName1", Calendar.getInstance(),  Calendar.getInstance(), "eventDescription1", 10, 0, false),
            new Event(2,"eventName2",  Calendar.getInstance(),  Calendar.getInstance(), "eventDescription2", 20, 10, false)
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
                    String name = category.getString(TAG_NAME);
                    String iconPath = category.getString(TAG_ICON);

                    result.add(new Category(name, iconPath));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
