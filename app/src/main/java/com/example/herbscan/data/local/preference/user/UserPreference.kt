package com.example.herbscan.data.local.preference.user

import android.content.Context

class UserPreference(context: Context) {
    private val preferece = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(data: User) {
        val editor = preferece.edit()
        editor.apply {
            putString("id", data.id)
            putBoolean(REMEMBER_ME, data.rememberMe)
            putBoolean(GOOGLE_AUTH, data.googleAuth)
            apply()
        }
    }

    fun getUser(): User {
        val user = User()
        user.apply {
            id = preferece.getString(ID, "")
            rememberMe = preferece.getBoolean(REMEMBER_ME, false)
            googleAuth = preferece.getBoolean(GOOGLE_AUTH, false)
        }
        
        return user
    }

    companion object {
        const val PREFS_NAME = "user_pref"
        const val ID = "id"
        const val REMEMBER_ME = "remember_me"
        const val GOOGLE_AUTH = "google_auth"
    }
}