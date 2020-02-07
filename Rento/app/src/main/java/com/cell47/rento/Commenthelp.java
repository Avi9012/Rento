package com.cell47.rento;

public class Commenthelp {
    private String com;
    private String userName;
    private String date;
    private String time;

    public Commenthelp(String com, String userName, String date, String time)
    {
        this.com=com;
        this.userName = userName;
        this.date = date;
        this.time = time;
    }

    public String getCom()
    {
        return com;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public void setTime(String time) {

        this.time = time;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public void setCom(String com) {

        this.com = com;
    }

    public Commenthelp()
    {

    }
}
