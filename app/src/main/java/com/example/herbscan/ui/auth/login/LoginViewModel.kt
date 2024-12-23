package com.example.herbscan.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.repository.HerbScanRepository

class LoginViewModel(private val repository: HerbScanRepository) : ViewModel() {
    fun login(email: String, password: String) =
        repository.login(email, password)
}