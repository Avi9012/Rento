package com.cell47.rento;

public class BlogMyAds {

    private String random;

    private String city;

    public BlogMyAds(String random, String city) {
        this.random = random;
        this.city = city;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getcity() {
        return city;
    }

    public void setcity(String city) {
        this.city = city;
    }

    public BlogMyAds(){

    }
}
