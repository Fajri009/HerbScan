package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: String = "",
    val uid: String = "",
    val profilePic: String = "",
    val name: String = "",
    val chat: String = "",
    val time: String = "",
): Parcelable