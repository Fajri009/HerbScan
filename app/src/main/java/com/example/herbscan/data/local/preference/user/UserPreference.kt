package com.example.herbscan.data.local.preference.user

import android.content.Context

class UserPreference(context: Context) {
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: User) {
        val editor = preference.edit()
        editor.apply {
            putString(UID, value.uid)
            putBoolean(REMEMBER_ME, value.rememberMe)
            apply()
        }
    }

    fun getUser(): User {
        val user = User()
        user.apply {
            uid = preference.getString(UID, "")
            rememberMe = preference.getBoolean(REMEMBER_ME, false)
        }

        return user
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val UID = "uid"
        private const val REMEMBER_ME = "remember_me"
    }
}