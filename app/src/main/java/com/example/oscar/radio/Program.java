package com.example.oscar.radio;

import android.support.annotation.IntegerRes;
import android.support.annotation.StringDef;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oszi on 1/31/17.
 */

public class Program {
    private int fromHour;
    private int fromMin;

    private int toHour;
    private int toMin;

    private int day;    // 1-7

    private String name;
    private String type;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        if(desc.length()==0)
            this.desc = "Nem elérhető leírás a műsorról.";
        else
            this.desc = desc.replace("; ", "\n\n")
                    .replace(". ", "\n")
                    .replace("Vendégek, témák: ", "Vendégek, témák:\n\n");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {  // Set the first letter to upper cased
        this.type = type.substring(0, 1).toUpperCase() + type.substring(1);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFromHour() {
        return fromHour;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }

    public int getFromMin() {
        return fromMin;
    }

    public void setFromMin(int fromMin) {
        this.fromMin = fromMin;
    }

    public int getToHour() {
        return toHour;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }

    public int getToMin() {
        return toMin;
    }

    public void setToMin(int toMin) {
        this.toMin = toMin;
    }


    private String convertToTimeStr(int h) {
        if(h==0)
            return "00";
        else if(h<10)
            return "0" + h;
        else
            return "" + h;  // lol
    }

    public String getFromTime() {
        return convertToTimeStr(fromHour) + ":" + convertToTimeStr(fromMin);
    }

    public String getToTime() {
        return convertToTimeStr(toHour) + ":" + convertToTimeStr(toMin);
    }

    public String getTimeStr() {
        return getFromTime() + " - " + getToTime();
    }

    public String getDayStr() {
        switch (day) {
            case 1:
                return "Hétfő";
            case 2:
                return "Kedd";
            case 3:
                return "Szerda";
            case 4:
                return "Csütörtök";
            case 5:
                return "Péntek";
            case 6:
                return "Szombat";
            case 7:
                return "Vasárnap";
            default:
                return "";
        }
    }

    public String getDayWithTime() {
        return getDayStr() + " " + getTimeStr();
    }


    private int getCurrentDay() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int day =  calendar.get(Calendar.DAY_OF_WEEK);  // First day of the week is Sunday

        if(--day == 0) day = 7;    // Now the first day of the week is Monday

        return day;
    }

    private int getCurrentHour() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date now = new Date();
        return Integer.parseInt(dateFormat.format(now));
    }

    private int getCurrentMin() {
        DateFormat dateFormat = new SimpleDateFormat("mm");
        Date now = new Date();
        return Integer.parseInt(dateFormat.format(now));
    }


    public void setDate(String raw, int day) {
        this.day = day;

        try {
            fromHour = Integer.parseInt(raw.substring(0, 2));
            fromMin = Integer.parseInt(raw.substring(3, 5));

            toHour = Integer.parseInt(raw.substring(8, 10));
            toMin = Integer.parseInt(raw.substring(11, 13));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            fromHour = 0;
            fromMin = 0;
            toHour = 0;
            toMin = 0;
        }
    }

    public boolean isPlayingNow() {

        if (getCurrentDay() != day)
            return false;
        if (getCurrentHour() < fromHour)
            return false;
        if(getCurrentHour() > toHour)
            return false;

        if (getCurrentHour() == fromHour)
            if (getCurrentMin() < fromMin)
                return false;

        if(getCurrentHour() == toHour)
            if(getCurrentMin() >= toMin)
                return false;

        return true;
    }


}
