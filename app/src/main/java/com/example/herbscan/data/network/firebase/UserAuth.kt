package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAuth(
    val id: String? = null,
    val profilePic: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String? = null
): Parcelable