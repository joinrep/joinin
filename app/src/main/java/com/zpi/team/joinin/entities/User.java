package com.zpi.team.joinin.entities;

import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class User {

    private String facebookId;
    private String firstName;
    private String lastName;

    private List<Category> favoriteCategories;
    private List<Event> organizedEvents;
    private List<Event> joinedEvents;
    private List<Comment> createdComments;
    private List<User> friends;
    private List<User> followers;

    public User(String facebookId, String firstName, String lastName) {
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Category> getFavoriteCategories() {
        return favoriteCategories;
    }

    public void setFavoriteCategories(List<Category> favoriteCategories) {
        this.favoriteCategories = favoriteCategories;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    public List<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(List<Event> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public List<Comment> getCreatedComments() {
        return createdComments;
    }

    public void setCreatedComments(List<Comment> createdComments) {
        this.createdComments = createdComments;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }
}
