package com.example.herbscan.ui.camera.result

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.local.room.HistoryEntity

class ResultViewModel(private val herbScanRepository: HerbScanRepository): ViewModel() {
    fun getPlantByName(namePlant: String) =
        herbScanRepository.getPlantByName(namePlant)

    fun insertHistory(history: HistoryEntity) =
        herbScanRepository.insertHistory(history)
}