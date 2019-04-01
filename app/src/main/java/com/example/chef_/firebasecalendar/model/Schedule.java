package com.example.chef_.firebasecalendar.model;

import java.util.Date;

public class Schedule {

    private int mId;
    private long mDate;
    private String mContent;

    public Schedule(){}

    public Schedule(long date, String content) {
        mDate = date;
        mContent = content;
    }

    public long getDate(){
        return mDate;
    }

    public void setDate(long date){
        mDate = date;
    }

    public String getContent(){
        return mContent;
    }

    public void setContent(String content){
        mContent = content;
    }
}
