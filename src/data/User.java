package data;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role; // admin, organizer, user
    private List<Ticket> tickets;

    public User(int userId, String firstName, String lastName, String email, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.tickets = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isOrganizer() {
        return "organizer".equalsIgnoreCase(role);
    }

    public boolean isParticipant() {
        return "user".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return String.format("User [ID=%d, Name=%s %s, Email=%s, Role=%s]",
                userId, firstName, lastName, email, role);
    }
}