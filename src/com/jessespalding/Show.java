package com.jessespalding;

import java.util.Date;

/**
 * Created by Jesse on 4/23/2015.
 */
public class Show {
    protected Date showDate;
    protected String showTime;
    protected String venueName;
    protected String streetName;
    protected String cityName;
    protected String stateName;
    protected double price;
    protected int soldTickets;
    protected int ticketFee;
    protected int merchFee;
    protected int otherFee;
    protected String venueContact;
    protected int distance;


    protected final static double EVENT_FINISHED = -1;

    public Show(Date date, String time, String venueName, String streetName, String cityName, String stateName,
                 String contact, double price, int soldTickets, int ticketFee, int merchFee,
                 int otherFee) {
        this.showDate = date;
        this.showTime = time;
        this.venueName = venueName;
        this.streetName = streetName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.venueContact = contact;
        this.price = price;
        this.soldTickets = soldTickets;
        this.ticketFee = ticketFee;
        this.merchFee = merchFee;
        this.otherFee = otherFee;
    }

    public String getVenueName() {
        return this.venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public int getTicketFee() {
        return ticketFee;
    }

    public void setTicketFee(int ticketFee) {
        this.ticketFee = ticketFee;
    }

    public int getMerchFee() {
        return merchFee;
    }

    public void setMerchFee(int merchFee) {
        this.merchFee = merchFee;
    }

    public int getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(int otherFee) {
        this.otherFee = otherFee;
    }

    public String getVenueContact() {
        return venueContact;
    }

    public void setVenueContact(String venueContact) {
        this.venueContact = venueContact;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public static double getEventFinished() {
        return EVENT_FINISHED;
    }
}
