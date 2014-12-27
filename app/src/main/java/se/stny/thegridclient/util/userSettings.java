
package se.stny.thegridclient.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import se.stny.thegridclient.ClientLoginActivity;
import se.stny.thegridclient.user;

public class userSettings {

    public static final String AGENT_NAME = "AGENT_NAME";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String USER_ID = "USER_ID";
    public static final String INNOVATOR = "INNOVATOR";
    private static final String PREF_NAME = "TheGridClientPrefs";
    private static final String IS_LOGIN = "IsLoggedIn";
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;

    // Constructor
    public userSettings(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String profile_pic, String UID, String INO) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(AGENT_NAME, name);
        editor.putString(IMAGE_URL, profile_pic);
        editor.putString(USER_ID, UID);
        editor.putString(INNOVATOR, INO);
        editor.commit();
    }

    public void checkLogin(boolean redirectLoggedOut, boolean redirectLoggedIn) {

        if (!this.isLoggedIn() && redirectLoggedOut) {
            Intent i = new Intent(_context, ClientLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        } else {
            if (redirectLoggedIn && this.isLoggedIn()) {
                Intent i = new Intent(_context, user.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(AGENT_NAME, pref.getString(AGENT_NAME, null));
        user.put(IMAGE_URL, pref.getString(IMAGE_URL, null));
        user.put(USER_ID, pref.getString(USER_ID, null));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        this.checkLogin(true, false);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


}