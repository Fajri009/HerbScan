package com.example.herbscan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.herbscan.di.Injection
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.ui.auth.AuthViewModel
import com.example.herbscan.ui.classify.ClassifyViewModel
import com.example.herbscan.ui.chat.ChatViewModel
import com.example.herbscan.ui.detail.DetailViewModel
import com.example.herbscan.ui.discussion.DiscussionViewModel
import com.example.herbscan.ui.home.HomeViewModel
import com.example.herbscan.ui.profile.ProfileViewModel
import com.example.herbscan.ui.profile.favorite.FavoriteViewModel
import com.example.herbscan.ui.profile.history.HistoryViewModel

class ViewModelFactory private constructor(private val herbScanRepository: HerbScanRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(herbScanRepository) as T
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
            modelClass.isAssignableFrom(ClassifyViewModel::class.java) -> {
                ClassifyViewModel(herbScanRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(herbScanRepository) as T
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