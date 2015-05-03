package com.zpi.team.joinin.repository;

import android.graphics.Color;

import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MK on 2015-03-19.
 */
public class UserRepository implements IRepository<User> {

    public static final String TAG_ORGANIZER = "organizer";
    public static final String TAG_PARTICIPANTS = "participants_array";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_FACEBOOK_ID = "facebook_id";
    public static final String TAG_GOOGLE_ID = "google_id";
    public static final String TAG_FIRST_NAME = "first_name";
    public static final String TAG_LAST_NAME = "last_name";

    public User getById(String userID) {
        User user = new User(1, null, null, "FName", "LName");
        user.setFavoriteCategories(Arrays.asList(new Category[]{new Category(1, "Piłka Nożna", "iconPath", Color.parseColor("#000000"))}));
        user.setFriends(Arrays.asList(new User[]{new User(2, null, null, "FName2", "LName2")}));
        return user;
    }

}
