package com.cell47.rento;

public class ImageUpload {
    private String Aval;
    private String Desc;
    private String Name;
    private String Price;
    private String Url;
    private String Cat;

    public String getAval() {
        return Aval;
    }

    public String getDesc() {
        return Desc;
    }

    public String getName() {
        return Name;
    }

    public String getPrice() {
        return Price;
    }

    public String getUrl() {
        return Url;
    }

    public String getCat() {
        return Cat;
    }

    ImageUpload(String aval, String desc, String name, String price, String url, String cat) {
        Aval = aval;
        Desc = desc;
        Name = name;
        Price = price;
        Url = url;
        Cat = cat;

    }
}