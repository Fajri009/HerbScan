package com.example.herbscan.ui.profile.favorite

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class FavoriteViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getAllFavorites(plantName: String) =
        repository.checkAllFavoritePlant(plantName)
}