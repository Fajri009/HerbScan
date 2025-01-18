package com.example.herbscan.ui.discussion

import androidx.lifecycle.ViewModel
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.data.network.firebase.Discussion

class DiscussionViewModel(private var herbScanRepository: HerbScanRepository): ViewModel() {
    fun getCurrentUser() =
        herbScanRepository.getCurrentUser()

    fun getDiscussion(plantName: String) =
        herbScanRepository.getDiscussion(plantName)

    fun addDiscussion(plantName: String, discussion: Discussion) =
        herbScanRepository.addDiscussion(plantName, discussion)

    fun addReply(plantName: String, discussionId: String, reply: Discussion) =
        herbScanRepository.addReply(plantName, discussionId, reply)
}