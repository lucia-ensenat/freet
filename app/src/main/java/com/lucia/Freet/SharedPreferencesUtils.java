package com.lucia.Freet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesUtils {
    public static void writeUsersPrefs(Context context, boolean isLogged, String email, String nickname) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogged", isLogged);
        editor.putString("email", email);
        editor.putString("nickname", nickname);
        editor.commit();

    }

    public static HashMap<String, String> getUsersPrefs(Context context) {
        final HashMap<String, String> preferences = new HashMap<>();
        final String keyLogged = "isLogged";
        final String keyEmail = "email";
        final String keyNickname = "nickname";
        final SharedPreferences sharedPreferences = context.getSharedPreferences("Prefs", MODE_PRIVATE);

        preferences.put(keyLogged, String.valueOf(sharedPreferences.getBoolean(keyLogged, false)));
        preferences.put(keyEmail, sharedPreferences.getString(keyEmail, null));
        preferences.put(keyNickname, sharedPreferences.getString(keyNickname, null));

        return preferences;
    }

    public static String get(String key, Context context){
        final HashMap<String, String> preferences = getUsersPrefs(context);
        return preferences.get(key);
    }
}
