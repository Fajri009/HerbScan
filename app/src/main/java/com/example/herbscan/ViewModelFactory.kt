package com.example.herbscan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.herbscan.di.Injection
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.ui.auth.forgotpass.ForgotPassViewModel
import com.example.herbscan.ui.auth.login.LoginViewModel
import com.example.herbscan.ui.auth.register.RegisterViewModel
import com.example.herbscan.ui.profile.ProfileViewModel
import com.example.herbscan.ui.profile.changePassword.ChangePasswordViewModel
import com.example.herbscan.ui.profile.editProfile.EditProfileViewModel

class ViewModelFactory private constructor(private val herbScanRepository: HerbScanRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ForgotPassViewModel::class.java) -> {
                ForgotPassViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) -> {
                ChangePasswordViewModel(herbScanRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository())
            }.also { instance = it }
    }
}