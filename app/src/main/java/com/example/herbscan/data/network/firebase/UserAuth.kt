package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAuth(
    val uid: String? = null,
    val profile_pic: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
): Parcelable