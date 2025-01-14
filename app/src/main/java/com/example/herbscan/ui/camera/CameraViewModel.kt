package com.example.herbscan.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.local.room.HistoryEntity

class CameraViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun classifyImage(image: Bitmap) =
        repository.classifyImage(image)

    fun insertHistory(history: HistoryEntity) =
        repository.insertHistory(history)
}