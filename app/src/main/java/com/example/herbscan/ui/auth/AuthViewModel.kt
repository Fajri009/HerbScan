package com.example.herbscan.ui.auth

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class AuthViewModel(private val repository: HerbScanRepository) : ViewModel() {
    fun register(authMap: HashMap<String, String>, userMap: HashMap<String, String>) =
        repository.register(authMap, userMap)

    fun login(email: String, password: String) =
        repository.login(email, password)

    fun changePass(email: String) =
        repository.changePassword(email)
}