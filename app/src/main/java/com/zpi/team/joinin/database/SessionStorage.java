package com.zpi.team.joinin.database;

import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MK on 2015-04-11.
 */
public class SessionStorage {

    private static SessionStorage instance = null;

    private SessionStorage(){};

    public static synchronized SessionStorage getInstance() {
        if (instance == null) {
            instance = new SessionStorage();
        }
        return instance;
    }

    private List<Category> categories = new ArrayList<Category>();
    private User user = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
