package com.cell47.rento;

public class ListSavedAds {
    String name,address,contact;
    Double latitude,longitude;

    public ListSavedAds(String name, String address, String contact,Double latitude,Double longitude) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.latitude=latitude;
        this.longitude=longitude;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ListSavedAds() {
    }
}
