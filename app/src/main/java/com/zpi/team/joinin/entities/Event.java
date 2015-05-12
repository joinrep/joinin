package com.zpi.team.joinin.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class Event {

    private int id;
    private String name;
    private Calendar startTime;
    private Calendar endTime;
    private String description;
    private String notes;
    private int limit;
    private double cost;
    private boolean canceled;
    private int participantsCount;
    private boolean participate;

    private Address location;
    private Category category;
    private User organizer;
    private List<User> participants = new ArrayList<User>();
    private List<Comment> comments = new ArrayList<Comment>();

    public Event(int id, String name, Calendar startTime, Calendar endTime, String description, String notes, int limit, double cost, boolean canceled, int participantsCount, boolean isParticipant) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.notes = notes;
        this.limit = limit;
        this.cost = cost;
        this.canceled = canceled;
        this.participantsCount = participantsCount;
        this.participate = isParticipant;
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

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public boolean getParticipate() {
        return participate;
    }

    public void setParticipate(boolean isParticipant) {
        this.participate = isParticipant;
    }
}
