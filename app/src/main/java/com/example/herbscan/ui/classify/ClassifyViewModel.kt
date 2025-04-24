package com.example.herbscan.ui.classify

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.PredictionResult

class ClassifyViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()

    fun classifyImage(image: Bitmap) =
        repository.classifyImage(image)

    fun addPredictionResult(result: PredictionResult, image: Uri, uid: String) =
        repository.addPredictionResult(result, image, uid)

    fun getPlantByName(namePlant: String) =
        repository.getPlantByName(namePlant)
}