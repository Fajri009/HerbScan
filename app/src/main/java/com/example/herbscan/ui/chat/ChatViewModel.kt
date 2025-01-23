package com.example.herbscan.ui.chat

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.Chat

class ChatViewModel(private var herbScanRepository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        herbScanRepository.getCurrentUser()

    fun getChat(plantName: String, discussionId: String) =
        herbScanRepository.getChat(plantName, discussionId)

    fun addChat(plantName: String, discussionId: String, chat: Chat) =
        herbScanRepository.addChat(plantName, discussionId, chat)

    fun deleteDiscussion(plantName: String, discussionId: String) =
        herbScanRepository.deleteDiscussion(plantName, discussionId)
}