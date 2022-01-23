package com.chatting.chatsapp.utils;

import java.util.Calendar;

public class TimeAgo {
    long timeInMillis;
    long milliSecPerMinute = 60 * 1000; //Milliseconds Per Minute
    long milliSecPerHour = milliSecPerMinute * 60; //Milliseconds Per Hour
    long milliSecPerDay = milliSecPerHour * 24; //Milliseconds Per Day
    long milliSecPerMonth = milliSecPerDay * 30; //Milliseconds Per Month
    long milliSecPerYear = milliSecPerDay * 365; //Milliseconds Per Year

    //Difference in Milliseconds between two dates
    public TimeAgo() {
    }

    public TimeAgo(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String convertToTimeAgo(long timeInMillis) {
        long currentDateTime = Calendar.getInstance().getTimeInMillis();
        long msExpired = currentDateTime - timeInMillis;
        if (msExpired < milliSecPerMinute) {
            if (Math.round(msExpired / 1000) == 1) {
                return String.valueOf(Math.round(msExpired / 1000)) + " second ago  ";
            } else {
                return String.valueOf(Math.round(msExpired / 1000) + " seconds ago ");
            }
        }
        //Minute or Minutes ago calculation
        else if (msExpired < milliSecPerHour) {
            if (Math.round(msExpired / milliSecPerMinute) == 1) {
                return String.valueOf(Math.round(msExpired / milliSecPerMinute)) + " minute ago  ";
            } else {
                return String.valueOf(Math.round(msExpired / milliSecPerMinute)) + " minutes ago  ";
            }
        }
        //Hour or Hours ago calculation
        else if (msExpired < milliSecPerDay) {
            if (Math.round(msExpired / milliSecPerHour) == 1) {
                return String.valueOf(Math.round(msExpired / milliSecPerHour)) + " hour ago  ";
            } else {
                return String.valueOf(Math.round(msExpired / milliSecPerHour)) + " hours ago  ";
            }
        }
        //Day or Days ago calculation
        else if (msExpired < milliSecPerMonth) {
            if (Math.round(msExpired / milliSecPerDay) == 1) {
                return String.valueOf(Math.round(msExpired / milliSecPerDay)) + " day ago  ";
            } else {
                return String.valueOf(Math.round(msExpired / milliSecPerDay)) + " days ago  ";
            }
        }
        //Month or Months ago calculation
        else if (msExpired < milliSecPerYear) {
            if (Math.round(msExpired / milliSecPerMonth) == 1) {
                return String.valueOf(Math.round(msExpired / milliSecPerMonth)) + "  month ago  ";
            } else {
                return String.valueOf(Math.round(msExpired / milliSecPerMonth)) + "  months ago  ";
            }
        }
        //Year or Years ago calculation
        else {
            if (Math.round(msExpired / milliSecPerYear) == 1) {
                return String.valueOf(Math.round(msExpired / milliSecPerYear)) + " year ago ";
            } else {
                return String.valueOf(Math.round(msExpired / milliSecPerYear)) + " years ago ";
            }
        }
    }
}
