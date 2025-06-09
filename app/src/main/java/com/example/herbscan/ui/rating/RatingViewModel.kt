package com.example.herbscan.ui.rating

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.Rating

class RatingViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()

    fun addRating(plantName: String, rating: Rating, imageUris: MutableList<Uri>) =
        repository.addRating(plantName, rating, imageUris)
}