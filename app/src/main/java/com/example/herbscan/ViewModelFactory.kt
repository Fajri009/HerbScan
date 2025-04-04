package com.example.herbscan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.herbscan.di.Injection
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.ui.auth.forgotpass.ForgotPassViewModel
import com.example.herbscan.ui.auth.login.LoginViewModel
import com.example.herbscan.ui.auth.register.RegisterViewModel
import com.example.herbscan.ui.camera.CameraViewModel
import com.example.herbscan.ui.camera.result.ResultViewModel
import com.example.herbscan.ui.chat.ChatViewModel
import com.example.herbscan.ui.detail.DetailViewModel
import com.example.herbscan.ui.detail.description.DescriptionViewModel
import com.example.herbscan.ui.discussion.DiscussionViewModel
import com.example.herbscan.ui.home.HomeViewModel
import com.example.herbscan.ui.profile.ProfileViewModel
import com.example.herbscan.ui.profile.changePassword.ChangePasswordViewModel
import com.example.herbscan.ui.profile.editProfile.EditProfileViewModel
import com.example.herbscan.ui.home.search.SearchViewModel
import com.example.herbscan.ui.profile.favorite.FavoriteViewModel
import com.example.herbscan.ui.profile.history.HistoryViewModel

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
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(DiscussionViewModel::class.java) -> {
                DiscussionViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(DescriptionViewModel::class.java) -> {
                DescriptionViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) -> {
                ChangePasswordViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(herbScanRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}