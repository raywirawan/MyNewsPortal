package com.example.mynewsportal.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUtils {

    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    public static String getTanggalFormat(String publishedAt) {

        String[] splitFormat = publishedAt.split("T");
        String date = splitFormat[0];
        String time = splitFormat[1].substring(0,5);
        String today = getCurrentDate();
        int day = Integer.parseInt(date.substring(8));
        int day_today = Integer.parseInt(today.substring(8));
        if (date.equals(today)){
            date = "Today";
        } else if (day_today - day == 1){
            date = "Yesterday";
        }
        return date;
    }
    public static String getJamFormat(String publishedAt){
        String[] splitFormat = publishedAt.split("T");
        String date = splitFormat[0];
        String time = splitFormat[1].substring(0,5);
        return time;
    }
}
