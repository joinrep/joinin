package com.zpi.team.joinin.repository;

import com.zpi.team.joinin.entities.Address;
import com.zpi.team.joinin.entities.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class EventRepository implements IRepository<Event> {
    @Override
    public Event getById() {
        try{Thread.sleep(1000);} catch (InterruptedException e){};
        Event event = new Event(1,"eventName1", new Date(), new Date(), "eventDescription1", 10, 0, false);
        return event;
    }

    @Override
    public List<Event> getAll() {
        try{Thread.sleep(1000);} catch (InterruptedException e){};
        List<Event> events = new ArrayList<Event>();

        events.add(new Event(1,"eventName1", new Date(), new Date(), "eventDescription1", 10, 0, false));
        events.add(new Event(2,"eventName2", new Date(), new Date(), "eventDescription2", 20, 10, false));
        events.add(new Event(3,"eventName3", new Date(), new Date(), "eventDescription3", 30, 0, false));
        events.add(new Event(4,"eventName4", new Date(), new Date(), "eventDescription3", 40, 30, false));
        return events;
    }

    @Override
    public void create(Event entity) {

    }

    @Override
    public void delete(Event entity) {

    }

    @Override
    public void update(Event entity) {

    }
}
