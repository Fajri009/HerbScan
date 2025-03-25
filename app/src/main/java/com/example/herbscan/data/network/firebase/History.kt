package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val uid: String = "",
    val prediction_id: String = ""
): Parcelable