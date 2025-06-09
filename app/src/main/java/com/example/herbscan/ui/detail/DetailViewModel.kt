package com.example.herbscan.ui.detail

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class DetailViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getCurrentPlant(plantName: String) =
        repository.getCurrentPlant(plantName)

    fun getCurrentUser() =
        repository.getCurrentUser()

    fun addFavoritePlant(plantId: String) =
        repository.addFavoritePlant(plantId)

    fun deleteFavoritePlant(plantId: String) =
        repository.deleteFavoritePlant(plantId)

    fun checkFavoritePlant(plantId: String) =
        repository.checkFavoritePlant(plantId)

    fun getManyDiscussion(plantName: String) =
        repository.getManyDiscussion(plantName)

    fun getDescription(plantId: String) =
        repository.getDescription(plantId)

    fun getUserRating(plantName: String) =
        repository.getUserRating(plantName)

    fun getAverageRating(plantName: String) =
        repository.getAverageRating(plantName)
}