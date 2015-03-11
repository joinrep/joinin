package com.zpi.team.joinin.entities;

import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class Address {

    private int id;
    private String city;
    private String street1;
    private String street2;
    private String locationName;

    private List<Event> events;

    public Address(int id, String city, String street1, String street2, String locationName) {
        this.id = id;
        this.city = city;
        this.street1 = street1;
        this.street2 = street2;
        this.locationName = locationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
