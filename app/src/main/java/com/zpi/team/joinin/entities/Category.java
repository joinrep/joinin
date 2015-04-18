package com.zpi.team.joinin.entities;

import java.util.List;

/**
 * Created by MK on 2015-03-04.
 */
public class Category {

    private int id;
    private String name;
    private String iconPath;
    private int iconId = -1;

    private boolean userFavorite = false;

    private List<User> subscribers;
    private List<Event> events;

    public Category(int id, String name, String iconPath) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public boolean isUserFavorite() {
        return userFavorite;
    }

    public void setUserFavorite(boolean userFavorite) {
        this.userFavorite = userFavorite;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}