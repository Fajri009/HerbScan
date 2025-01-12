package com.example.herbscan.ui.camera.result

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class ResultViewModel(private val herbScanRepository: HerbScanRepository): ViewModel() {
    fun getIPlantByName(namePlant: String) =
        herbScanRepository.getPlantByName(namePlant)
}