package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val profilePic: String = "",
    val rating: Int = 0,
    val desc: String = "",
    val time: String = "",
    var image1: String = "",
    var image2: String = "",
    var image3: String = "",
    var image4: String = "",
    var image5: String = ""
): Parcelable