package com.example.herbscan.ui.auth.forgotpass

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class ForgotPassViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun forgotPass(email: String) =
        repository.forgotPass(email)
}