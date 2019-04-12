package com.sudjunham.boonyapon;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Event_list {

    String name, date,location , content , imglink,sponsor,phonecontact,website,dateTimeST,dateTimeED;
    int monthForFilter;
    private static final Event_list instance = new Event_list();
    @ParcelConstructor
    public Event_list() {
        this.date = date;
        this.name = name;
        this.location = location;
        this.monthForFilter = monthForFilter;
        this.content = content;
        this.imglink = imglink;
        this.sponsor = sponsor;
        this.phonecontact = phonecontact;
        this.website = website;
        this.dateTimeST = dateTimeST;
        this.dateTimeED = dateTimeED;



    }
    public static Event_list getInstance() {
        return instance;
    }


    public String getDateTimeST() {
        return dateTimeST;
    }

    public void setDateTimeST(String dateTimeST) {
        this.dateTimeST = dateTimeST;
    }

    public String getDateTimeED() {
        return dateTimeED;
    }

    public void setDateTimeED(String dateTimeED) {
        this.dateTimeED = dateTimeED;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getPhonecontact() {
        return phonecontact;
    }

    public void setPhonecontact(String phonecontact) {
        this.phonecontact = phonecontact;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getmonthForFilter() {
        return monthForFilter;
    }

    public void setmonthForFilter(int monthForFilter) {
        this.monthForFilter = monthForFilter;
    }

    public void setLocation(String location) {
        this.location = location; }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}