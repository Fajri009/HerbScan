package com.example.herbscan.data.local.preference.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var uid: String? = "",
    var rememberMe: Boolean = false
): Parcelable