package com.example.herbscan.ui.detail

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.Plant

class DetailViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun addFavoritePlant(plantName: Plant) =
        repository.addFavoritePlant(plantName)

    fun deleteFavoritePlant(plantName: String) =
        repository.deleteFavoritePlant(plantName)

    fun checkFavoritePlant(plantName: String) =
        repository.getFavoritePlant(plantName)

    fun getManyDiscussion(plantName: String) =
        repository.getManyDiscussion(plantName)
}