package com.cell47.rento;

public class Blog {

    private String price;
    private String tagline;
    private String thumbnail;
    private  String address;
    private String status;


    public Blog(String price,String tagline,String thumbnail,String address,String status){

        this.price =price;
        this.tagline=tagline;
        this.thumbnail=thumbnail;
        this.address=address;
        this.status=status;

    }

    public String getStatus(){ return status; }

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address) {
        this.address = address;
    }

    public void setStatus(){this.status=status;}

    public Blog(){

    }
}
