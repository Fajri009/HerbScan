package com.example.herbscan.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val picture: String = "",
    val name: String = "",
    val scientificName: String = "",
    val recommendation: String = "",
    val availability: String = "",
    val habitat: String = "",
    val home: Home = Home(),
): Parcelable

@Parcelize()
data class Home(
    val category: String = "",
    val part: String = "",
): Parcelable