package com.example.herbscan.ui.detail.description

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class DescriptionViewModel(private var repository: HerbScanRepository): ViewModel() {
    fun getCurrentPlant(plantName: String) =
        repository.getCurrentPlant(plantName)

    fun getDescription(plantId: String) =
        repository.getDescription(plantId)
}