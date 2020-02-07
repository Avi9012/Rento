package com.cell47.rento;

public class AdUploadedBlog {
    private String price;
    private String tagline;

    public AdUploadedBlog(String price,String tagline){
        this.price =price;
        this.tagline=tagline;

    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public AdUploadedBlog(){

    }
}
