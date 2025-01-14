package com.example.herbscan.ui.profile.history

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class HistoryViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun getHistoryByName(userId: String, namePlant: String) =
        repository.getHistoryByName(userId, namePlant)
}