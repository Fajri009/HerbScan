package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Discussion(
    val id: String = "",
    val uid: String = "",
    val profilePic: String = "",
    val name: String = "",
    val title: String = "",
    val time: String = "",
): Parcelable

