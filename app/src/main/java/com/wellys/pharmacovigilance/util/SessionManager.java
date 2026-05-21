package com.wellys.pharmacovigilance.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Tiny wrapper over SharedPreferences for tracking which demo user is logged in.
 * Not suitable for real auth — there are no tokens, no encryption.
 */
public class SessionManager {

    private static final String PREFS_NAME = "pharmaco_session";
    private static final String KEY_USER_ID = "user_id";
    private static final long NO_USER = -1L;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setUserId(long id) {
        prefs.edit().putLong(KEY_USER_ID, id).apply();
    }

    /** @return the logged-in user id, or -1 if no one is logged in. */
    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, NO_USER);
    }

    public boolean isLoggedIn() {
        return getUserId() != NO_USER;
    }

    public void logout() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}
