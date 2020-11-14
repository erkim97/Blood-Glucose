package com.bcit.ma_kim;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.util.Date;

public class BloodPressureReading implements Serializable {
    public String id;
    public String spUser;
    public String time;
    public String date;
    public String systolicReading;
    public String diastolicReading;
    public String condition;

    public BloodPressureReading() {}


    public BloodPressureReading(String spUser, String systolicReading,
                                        String diastolicReading) {

        this.id = String.valueOf(System.currentTimeMillis());
        this.spUser = spUser;


        //Datetime string
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateTime = dateTimeFormatter.format(currentDate);
        String[] splitDateTime = dateTime.split(" ");
        this.date = splitDateTime[0];
        this.time = splitDateTime[1];

        this.systolicReading = systolicReading;
        this.diastolicReading = diastolicReading;

        //Readings into ints for comparison
        int systolicReadingInt = Integer.parseInt(this.systolicReading);
        int diastolicReadingInt = Integer.parseInt(this.diastolicReading);
        if(systolicReadingInt > 180 || diastolicReadingInt > 120){
            this.condition = ConditionTypes.HYPERTENSIVE.toString();
        } else if(systolicReadingInt >= 140 || diastolicReadingInt >= 90){
            this.condition = ConditionTypes.STAGE2.toString();
        } else if(systolicReadingInt >= 130 || diastolicReadingInt >= 80){
            this.condition = ConditionTypes.STAGE1.toString();
        } else if(systolicReadingInt >= 120){
            this.condition = ConditionTypes.ELEVATED.toString();
        } else {
            this.condition = ConditionTypes.NORMAL.toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpUser() {
        return spUser;
    }

    public void setSpUser(String spUser) {
        this.spUser = spUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateTime = dateTimeFormatter.format(currentDate);
        String[] splitDateTime = dateTime.split(" ");
        this.date = splitDateTime[0];
        this.time = splitDateTime[1];
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateTime = dateTimeFormatter.format(currentDate);
        String[] splitDateTime = dateTime.split(" ");
        this.date = splitDateTime[0];
        this.time = splitDateTime[1];
    }

    public String getSystolicReading() {
        return systolicReading;
    }

    public void setSystolicReading(String systolicReading) {
        this.systolicReading = systolicReading;
    }

    public String getDiastolicReading() {
        return diastolicReading;
    }

    public void setDiastolicReading(String diastolicReading) {
        this.diastolicReading = diastolicReading;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition() {
        int systolicReadingInt = Integer.parseInt(this.getSystolicReading());
        int diastolicReadingInt = Integer.parseInt(this.getDiastolicReading());
        if(systolicReadingInt > 180 || diastolicReadingInt > 120){
            this.condition = ConditionTypes.HYPERTENSIVE.toString();
        } else if(systolicReadingInt >= 140 || diastolicReadingInt >= 90){
            this.condition = ConditionTypes.STAGE2.toString();
        } else if(systolicReadingInt >= 130 || diastolicReadingInt >= 80){
            this.condition = ConditionTypes.STAGE1.toString();
        } else if(systolicReadingInt >= 120){
            this.condition = ConditionTypes.ELEVATED.toString();
        } else {
            this.condition = ConditionTypes.NORMAL.toString();
        }
    }
}

//Possible condition types.
enum ConditionTypes{
    NORMAL, ELEVATED, STAGE1, STAGE2, HYPERTENSIVE
}

