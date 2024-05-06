package com.example.myapplication;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarUtil {
    private int year;
    private int month;
    private int day;

    public CalendarUtil() {
        Calendar calendar = new GregorianCalendar();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month + 1; // Add 1 to get the month as a value between 1 and 12
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        return month + "/" + day + "/" + year;
    }
}