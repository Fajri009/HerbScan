package com.example.herbscan.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class ProfileViewModel(private val repository: HerbScanRepository) : ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()

    fun updateCurrentUser(imageUri: Uri, userMap: HashMap<String, String>) =
        repository.updateCurrentUser(imageUri, userMap)

    fun changePassword(email: String) =
        repository.changePassword(email)
}