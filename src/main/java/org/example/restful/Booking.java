package org.example.restful;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Booking {
    private int location_number;
    private String client;
    private String agency;
    private double price;
    private String room;
    private String hotel;
    private String checkIn;
    private int roomNights;

    public Booking(){

    }
    public Booking(int location_number, String client, String agency, double price, String room, String hotel, String checkIn, int roomNights) {
        this.location_number = location_number;
        this.client = client;
        this.agency = agency;
        this.price = price;
        this.room = room;
        this.hotel = hotel;
        this.checkIn = checkIn;
        this.roomNights = roomNights;
    }

    public int getLocation_number() {
        return location_number;
    }

    public void setLocation_number(int location_number) {
        this.location_number = location_number;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public int getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(int roomNights) {
        this.roomNights = roomNights;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "location_number=" + location_number +
                ", client='" + client + '\'' +
                ", agency='" + agency + '\'' +
                ", price=" + price +
                ", room='" + room + '\'' +
                ", hotel='" + hotel + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", roomNights=" + roomNights +
                '}';
    }
}