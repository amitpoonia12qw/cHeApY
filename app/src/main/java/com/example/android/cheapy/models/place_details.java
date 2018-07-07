package com.example.android.cheapy.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/* although there is a inbuilt class "Place" which can store all the details about a place but in the end we have release the memory of inbuilt Place Object.
so here we are creating our own class to store info about a place. so we don't need to release memory of this object and we can keep these data stored.
 */

public class place_details {

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private String attributions;

    private LatLng latLng;
    private float rating;
    private Uri websiteUri;

    public place_details(String name, String address, String phoneNumber, String id,
                         String attributions, LatLng latLng, float rating, Uri websiteUri){

        this.address = address;
        this.name = name;
        this.attributions = attributions;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.id = id;
        this.latLng = latLng;
        this.rating = rating;
    }
    //empty constructor
    public place_details(){

    }

    //getter and setter methods.....

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    //override toString method to get all details as an string
    @Override
    public String toString() {
        return "place_details{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", attributions='" + attributions + '\'' +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", websiteUri=" + websiteUri +
                '}';
    }
}
