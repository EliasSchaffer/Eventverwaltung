package data;

import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private int userId;
    private int eventId;
    private int typeId;
    private LocalDateTime bookingDate;
    private boolean redeemed;
    private String vorName;
    private String nachName;

    public Ticket(int ticketId, int userId, int eventId, int typeId,
                  LocalDateTime bookingDate, boolean redeemed, String vorName, String nachName) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.eventId = eventId;
        this.typeId = typeId;
        this.bookingDate = bookingDate;
        this.redeemed = redeemed;
        this.vorName = vorName;
        this.nachName = nachName;
    }

    // Constructor for creating a new ticket (without ticketId)
    public Ticket(int userId, int eventId, int typeId, String vorName, String nachName) {
        this.userId = userId;
        this.eventId = eventId;
        this.typeId = typeId;
        this.bookingDate = LocalDateTime.now();
        this.redeemed = false;
        this.vorName = vorName;
        this.nachName = nachName;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getTypeId() {
        return typeId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public String getVorName() {
        return vorName;
    }

    public String getNachName() {
        return nachName;
    }

    @Override
    public String toString() {
        return String.format("Ticket [ID=%d, Event=%d, Type=%d, User=%d, Name=%s %s, Redeemed=%b]",
                ticketId, eventId, typeId, userId, vorName, nachName, redeemed);
    }
}