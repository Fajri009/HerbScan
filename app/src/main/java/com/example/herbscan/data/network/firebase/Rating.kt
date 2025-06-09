package com.example.herbscan.data.network.firebase

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val id: String,
    val uid: String,
    val profilePic: String,
    val name: String,
    val rating: Float,
    val desc: String,
    var image1: String? = null,
    var image2: String? = null,
    var image3: String? = null,
    var image4: String? = null,
    var image5: String? = null,
    val time: String
): Parcelable