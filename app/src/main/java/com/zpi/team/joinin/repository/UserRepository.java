package com.zpi.team.joinin.repository;

import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MK on 2015-03-19.
 */
public class UserRepository implements IRepository {

    public User getById(String userID) {
        User user = new User("1", "FName", "LName");
        user.setFavoriteCategories(Arrays.asList(new Category[]{new Category("Piłka Nożna", "iconPath")}));
        user.setFriends(Arrays.asList(new User[]{new User("2", "FName2", "LName2")}));
        return user;
    }
    
    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void create(Object entity) {

    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void update(Object entity) {

    }
}
