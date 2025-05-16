package data;

public class Location {
    private int locationId;
    private String location;
    private String city;
    private String street;
    private int streetNumber;

    public Location(int locationId, String location, String city, String street, int streetNumber) {
        this.locationId = locationId;
        this.location = location;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    // Constructor for creating a new location (without locationId)
    public Location(String location, String city, String street, int streetNumber) {
        this.location = location;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getFullAddress() {
        return String.format("%s, %s %d, %s", location, street, streetNumber, city);
    }

    @Override
    public String toString() {
        return String.format("Location [ID=%d, Name=%s, Address=%s %d, %s]",
                locationId, location, street, streetNumber, city);
    }
}
