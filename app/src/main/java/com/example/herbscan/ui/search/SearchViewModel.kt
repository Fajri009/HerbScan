package com.example.herbscan.ui.search

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class SearchViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getPlantByName(namePlant: String) =
        repository.getPlantByName(namePlant)
}