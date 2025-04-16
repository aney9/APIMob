package com.example.kurs;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthUtils {
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_USER_ID = "user_id";

    public static void saveUserId(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ID, null);
    }

    public static void clearUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getUserId(context) != null;
    }
}