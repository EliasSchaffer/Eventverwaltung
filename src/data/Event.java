package data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private int locationId;
    private String category;
    private int organizerId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int maxTickets;
    private int availableTickets;
    private List<Ticket> tickets;

    public Event(int eventId, String title, String description, int locationId,
                 String category, int organizerId, LocalDateTime fromDate,
                 LocalDateTime toDate, int maxTickets) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.locationId = locationId;
        this.category = category;
        this.organizerId = organizerId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.maxTickets = maxTickets;
        this.availableTickets = maxTickets;
        this.tickets = new ArrayList<>();
    }

    // Constructor for creating a new event (without eventId)
    public Event(String title, String description, int locationId, String category,
                 int organizerId, LocalDateTime fromDate, LocalDateTime toDate, int maxTickets) {
        this.title = title;
        this.description = description;
        this.locationId = locationId;
        this.category = category;
        this.organizerId = organizerId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.maxTickets = maxTickets;
        this.availableTickets = maxTickets;
        this.tickets = new ArrayList<>();
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public int getMaxTickets() {
        return maxTickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public boolean hasAvailableTickets() {
        return availableTickets > 0;
    }

    public boolean bookTicket() {
        if (availableTickets > 0) {
            availableTickets--;
            return true;
        }
        return false;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        return String.format("Event [ID=%d, Title=%s, Category=%s, From=%s, To=%s, Available Tickets=%d/%d]",
                eventId, title, category, fromDate, toDate, availableTickets, maxTickets);
    }
}
