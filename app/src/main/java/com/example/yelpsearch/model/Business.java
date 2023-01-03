package com.example.yelpsearch.model;
import androidx.annotation.NonNull;

import java.io.Serializable;

public class Business implements Serializable{

    private String business_id;
    private String name;
    private String image_url;
    private String rating;
    private String distance_meters;
    private String distance_miles;
    private String url;
    private String table_index;

    public Business(){

    }

    public Business(String business_id, String name, String image_url, String rating, String distance_meters, String url, String table_index) {
        this.business_id = business_id;
        this.name = name;
        this.image_url = image_url;
        this.rating = rating;
        this.distance_meters = distance_meters;
        this.url = url;
        this.table_index = table_index;
    }

    public void convertToMiles() {
        double miles = Double.parseDouble(distance_meters)*0.000621371;
        distance_miles = String.format("%.2f", miles);
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDistance_meters() {
        return distance_meters;
    }

    public void setDistance_meters(String distance_meters) {
        this.distance_meters = distance_meters;
    }

    public String getDistance_miles() {
        return distance_miles;
    }

    public void setDistance_miles(String distance_miles) {
        this.distance_miles = distance_miles;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTable_index() {return table_index;}

    public void setTable_index(String table_index) {this.table_index = table_index;}

    @NonNull
    @Override
    public String toString() {
        String display = this.name + " " + this.rating + " " + this.distance_miles + " " + this.image_url;
        return display;
    }
}