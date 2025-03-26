package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Description(
    val benefit: String = "",
    val how_to_use: String = "",
    val side_effect: String = ""
): Parcelable