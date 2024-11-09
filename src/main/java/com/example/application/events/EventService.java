package com.example.application.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class EventService {
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    // Additional methods as needed
}