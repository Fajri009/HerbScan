package com.example.herbscan.data.local.preference.classification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Features(
    var clahe: Boolean = false,
    var colorConversion: Boolean = false,
    var dataAugmentation: Boolean = false
): Parcelable
