package com.example.vivaaidemo.demo.common;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.validator.routines.UrlValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean isUrl(String link) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(link);
    }

    public static long dateToMillisecond(String text, String format) {
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(text);
            c.setTime(date);
            return c.getTimeInMillis();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + text, e);
        }
    }

    public static String millisecondToString(long millisecond) {
        return millisecondToString(millisecond, "dd/MM/yyyy HH:mm:ss");
    }

    public static String millisecondToString(long millisecond, String format) {
        if (millisecond < 0) return "";
        else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(millisecond);
            return DateFormat.format(format, c).toString();
        }
    }
}

