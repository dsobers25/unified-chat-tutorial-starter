package com.example.application.events;
import java.time.LocalDate;
import java.util.Objects;

public class Event {
    private String title;
    private String details;
    private String imageUrl;
    private LocalDate date;  // Added date field

    public Event(String title, String details, String imageUrl, LocalDate date) {
        this.title = title;
        this.details = details;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(title, event.title) &&
               Objects.equals(date, event.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, date);
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
