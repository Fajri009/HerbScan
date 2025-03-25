package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictionResult(
    val time: String = "",
    val image: String = "",
    val prediction: String = "",
    val probability: String = "",
    val inference_time: String = "",
): Parcelable