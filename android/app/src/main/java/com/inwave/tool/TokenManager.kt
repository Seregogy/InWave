package com.inwave.tool

import android.content.Context
import android.content.SharedPreferences

class TokenManager(
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun getTokenForce(): String {
        return prefs.getString("jwt_token", null)!!
    }

    fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }

    fun hasToken(): Boolean = getToken() != null
}