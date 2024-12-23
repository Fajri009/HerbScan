package com.example.herbscan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.herbscan.di.Injection
import com.example.herbscan.data.repository.HerbScanRepository
import com.example.herbscan.ui.auth.forgotpass.ForgotPassViewModel
import com.example.herbscan.ui.auth.login.LoginViewModel
import com.example.herbscan.ui.auth.register.RegisterViewModel

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