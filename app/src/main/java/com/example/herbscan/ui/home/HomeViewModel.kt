package com.example.herbscan.ui.home

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class HomeViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()

    fun getCategory() =
        repository.getCategory()

    fun getAllPlant() =
        repository.getAllPlant()

    fun getImagePlant(plantName: String) =
        repository.getImagePlant(plantName)
}