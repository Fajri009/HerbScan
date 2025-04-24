package com.example.herbscan.ui.chat

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.Chat

class ChatViewModel(private var repository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        repository.getCurrentUser()

    fun getChat(plantName: String, discussionId: String) =
        repository.getChat(plantName, discussionId)

    fun addChat(plantName: String, discussionId: String, chat: Chat) =
        repository.addChat(plantName, discussionId, chat)

    fun deleteDiscussion(plantName: String, discussionId: String) =
        repository.deleteDiscussion(plantName, discussionId)
}