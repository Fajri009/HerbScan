package com.example.herbscan.ui.profile.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository

class EditProfileViewModel(private val repository: HerbScanRepository): ViewModel() {
    fun updateCurrentUser(imageUri: Uri, userMap: HashMap<String, String>) =
        repository.updateCurrentUser(imageUri, userMap)
}