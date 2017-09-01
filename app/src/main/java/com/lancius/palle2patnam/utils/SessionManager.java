package com.lancius.palle2patnam.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lancius.palle2patnam.activity.LoginActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref, lpref;

    Editor editor, leditor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "CapitalShowPref";
    private static final String PREF_LOGIN = "CapitalLoginPref";

    private static final String IS_SHOWING = "IsShowing";
    private static final String IS_LOGIN = "IsLogged";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        lpref = _context.getSharedPreferences(PREF_LOGIN, PRIVATE_MODE);
        leditor = lpref.edit();
    }

    public void createLoginSession(String email, String id, String phone, String name) {

        //Log.d("Email Id Sessions", language);
        // Storing login value as TRUE
        leditor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        leditor.putString(KEY_EMAIL, email);
        leditor.putString(KEY_ID, id);
        leditor.putString(KEY_PHONE, phone);
        leditor.putString(KEY_NAME, name);

        // commit changes
        leditor.commit();
    }

    public void createLoginSession() {

        //Log.d("Email Id Sessions", language);
        // Storing login value as TRUE
        leditor.putBoolean(IS_LOGIN, true);

        // commit changes
        leditor.commit();
    }

    public void showingSession() {

        editor.putBoolean(IS_SHOWING, true);
        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_EMAIL, lpref.getString(KEY_EMAIL, null));
        user.put(KEY_ID, lpref.getString(KEY_ID, null));
        user.put(KEY_PHONE, lpref.getString(KEY_PHONE, null));
        user.put(KEY_NAME, lpref.getString(KEY_NAME, null));

        // return user
        return user;
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        leditor.clear();
        leditor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    public boolean isLogged() {
        return lpref.getBoolean(IS_LOGIN, false);
    }

    public boolean isShown() {
        return pref.getBoolean(IS_SHOWING, false);
    }
}
