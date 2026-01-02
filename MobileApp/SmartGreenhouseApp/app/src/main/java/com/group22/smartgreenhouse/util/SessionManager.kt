package com.group22.smartgreenhouse.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private lateinit var prefs: SharedPreferences
    private const val PREF_NAME = "smart_greenhouse_prefs"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var jwtToken: String?
        get() = prefs.getString("jwt_token", null)
        set(value) = prefs.edit().putString("jwt_token", value).apply()

    var userId: String?
        get() = prefs.getString("user_id", null)
        set(value) = prefs.edit().putString("user_id", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()

    var userName: String?
        get() = prefs.getString("user_name", null)
        set(value) = prefs.edit().putString("user_name", value).apply()

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
