package com.rsf.sms_reader

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager(context: Context) {
    val APP_PREFERENCES = "mysharedprefs"

    private var prefs: SharedPreferences = context.getSharedPreferences(
        APP_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }


    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

}
