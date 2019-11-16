package com.example.mytasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {
    public static String FromDbToDisplay(String datetime) {
        if (!datetime.equals("")) {
            String[] temp = datetime.split(" ");
            String[] date = temp[0].split("-");
            String year = date[0];
            String month = date[1];
            if (month.length() == 1) month = "0" + month;
            String day = date[2];
            if (day.length() == 1) day = "0" + day;
            String[] time = temp[1].split(":");
            String hour = time[0];
            if (hour.length() == 1) hour = "0" + hour;
            String minute = time[1];
            if (minute.length() == 1) minute = "0" + minute;
            return String.format("%s:%s ngày %s/%s/%s", hour, minute, day, month, year);
        }
        return "";
    }


    public static String FromDateToDb(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date FromDbToDate(String dateFromDb) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateFromDb);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date AddDate(Date date, int type, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        while ((new Date().getTime() - c.getTime().getTime()) > 0) {
            c.add(type, offset);
        }
        return c.getTime();
    }

    public static Boolean DatePassed(Date date) {
        return (new Date().getTime() - date.getTime()) > 0;
    }
}
