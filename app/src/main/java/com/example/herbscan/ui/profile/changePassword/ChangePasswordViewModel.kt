package com.example.herbscan.ui.profile.changePassword

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class ChangePasswordViewModel(private val repository: HerbScanRepository) : ViewModel() {
    fun changePassword(email: String) =
        repository.changePassword(email)
}