package com.example.herbscan.ui.profile

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class ProfileViewModel(private val repository: HerbScanRepository) : ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()
}