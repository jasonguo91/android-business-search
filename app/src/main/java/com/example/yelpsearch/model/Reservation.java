package com.example.yelpsearch.model;

import java.io.Serializable;

public class Reservation implements Serializable, Comparable<Reservation> {
    private int index;
    private String business_name;
    private String date;
    private String time;
    private String email;

    public Reservation(int index, String business_name, String date, String time, String email) {
        this.index = index;
        this.business_name = business_name;
        this.date = date;
        this.time = time;
        this.email = email;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Reservation{ " +
                "index= " + index +
                "business_name='" + business_name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public int compareTo(Reservation comparestu) {
        int compareIndex =
                ((Reservation)comparestu).getIndex();
        return this.index - compareIndex;
    }
}
