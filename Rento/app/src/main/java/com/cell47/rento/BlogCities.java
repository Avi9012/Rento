package com.cell47.rento;

public class BlogCities {

    private String postalcode;
    private String city;

    public BlogCities(String postalcode, String city) {
        this.postalcode = postalcode;
        this.city = city;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BlogCities(){

    }
}
