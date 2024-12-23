package com.example.herbscan.ui.auth.register

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.repository.HerbScanRepository

class RegisterViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun register(authMap: HashMap<String, String>, userMap: HashMap<String, String>) =
        repository.register(authMap, userMap)
}