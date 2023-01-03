package com.example.yelpsearch.model;

import java.io.Serializable;

public class Review implements Serializable {
    private String user_name;
    private String text;
    private String rating;
    private String time_created;

    public Review()  {

    }

    public Review(int index, String user_name, String text, String rating, String time_created) {
        this.user_name = user_name;
        this.text = text;
        this.rating = rating;
        this.time_created = time_created;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    @Override
    public String toString() {
        return "Review{" +
                "user_name='" + user_name + '\'' +
                ", text='" + text + '\'' +
                ", rating='" + rating + '\'' +
                ", time_created='" + time_created + '\'' +
                '}';
    }

}
