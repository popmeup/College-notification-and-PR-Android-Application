package com.telecom.project4t.testactivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Event {

    String content;
    String fileURL;
    String imageURL;
    String organize;
    String place;
    String time;
    String title;

    public ArrayList<String> returnArrayList(){
        ArrayList<String> arrayList = new ArrayList<>() ;
        arrayList.add(content);
        arrayList.add(fileURL);
        arrayList.add(imageURL);
        arrayList.add(organize);
        arrayList.add(place);
        arrayList.add(time);
        arrayList.add(title);
        return arrayList;
    }

    public Event() {

    }

    public Event(String content, String fileURL, String imageURL, String organize, String place, String time, String title) {
        this.content = content;
        this.fileURL = fileURL;
        this.imageURL = imageURL;
        this.organize = organize;
        this.place = place;
        this.time = time;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOrganize() {
        return organize;
    }

    public void setOrganize(String organize) {
        this.organize = organize;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
