package com.example.kurs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthUtils {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_CLIENT_NAME = "clientName"; // Новое поле
    private static final String TAG = "AuthUtils";

    // Сохранить userId и clientName
    public static void saveUserData(Context context, int userId, String clientName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_CLIENT_NAME, clientName);
        editor.apply();
        Log.d(TAG, "Сохранено: userId=" + userId + ", clientName=" + clientName);
    }

    // Получить числовой userId
    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Получен userId: " + userId);
        return userId;
    }

    // Получить clientName
    public static String getClientName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String clientName = prefs.getString(KEY_CLIENT_NAME, null);
        Log.d(TAG, "Получен clientName: " + clientName);
        return clientName;
    }

    // Очистить данные
    public static void clearUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_CLIENT_NAME);
        editor.apply();
        Log.d(TAG, "Данные пользователя очищены");
    }

    // Проверка авторизации
    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = prefs.getInt(KEY_USER_ID, -1) != -1 && prefs.getString(KEY_CLIENT_NAME, null) != null;
        Log.d(TAG, "Пользователь авторизован: " + loggedIn);
        return loggedIn;
    }
}