package com.example.vivaaidemo.demo.common;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.validator.routines.UrlValidator;

public class Utility {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    public static boolean isUrl(String link) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(link);
    }
}

