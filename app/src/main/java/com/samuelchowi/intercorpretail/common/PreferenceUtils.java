package com.samuelchowi.intercorpretail.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.samuelchowi.intercorpretail.R;

public class PreferenceUtils {

    private static PreferenceUtils instance;
    private static String PHONE_NUMBER_KEY = "phone_number_key";

    private final SharedPreferences preferences;

    private PreferenceUtils(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static PreferenceUtils instance(Context context) {
        if (instance == null) instance = new PreferenceUtils(context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE));
        return instance;
    }

    public String getPhoneNumber() {
        return preferences.getString(PHONE_NUMBER_KEY, "");
    }

    public void setPhoneNumber(String phoneNumber) {
        preferences.edit().putString(PHONE_NUMBER_KEY, phoneNumber).apply();
    }
}
