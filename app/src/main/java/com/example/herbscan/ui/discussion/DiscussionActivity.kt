package com.example.herbscan.ui.discussion

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.DiscussionAdapter
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.databinding.ActivityDiscussionBinding
import com.example.herbscan.utils.Utils
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.UserAuth

class DiscussionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscussionBinding
    private val viewModel by viewModels<DiscussionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var user: UserAuth? = null
    private lateinit var discussionAdapter: DiscussionAdapter
    private var replyState: Boolean = false
    private var discussionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plantName = intent.getStringExtra(PLANT_NAME)

        getDiscussion(plantName!!)
        getCurrentUser()

        binding.apply {
            tvPlantName.setText("Diskusi ($plantName)")
            ivBack.setOnClickListener { finish() }
            rvDiscussion.layoutManager = LinearLayoutManager(this@DiscussionActivity)
            ivSendMessage.setOnClickListener {
                if (!replyState) {
                    sendMessage(plantName!!)
                } else {
                    addReply(plantName!!, discussionId)
                }
            }
            layoutReply.setOnClickListener { hideReplyLayout() }
        }
    }

    private fun getDiscussion(plantName: String) {
        viewModel.getDiscussion(plantName).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading ->  {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val discussion = result.data

                        discussionAdapter = DiscussionAdapter(discussion)
                        binding.rvDiscussion.adapter = discussionAdapter
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showReplyLayout(discussion: Discussion) {
        binding.layoutReply.visibility = View.VISIBLE
        binding.tvReply.text = discussion.chat
        replyState = true
    }

    private fun hideReplyLayout() {
        binding.layoutReply.visibility = View.GONE
        replyState = false
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        user = result.data
                        Log.i(TAG, "getCurrentUser: $user")
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun sendMessage(plantName: String) {
        val userInput = binding.etUserInputMessage.text.toString()
        val fullName = user!!.firstName + " " + user!!.lastName

        if (userInput.isNotEmpty()) {
            Log.i(TAG, "sendMessage: @${user!!.id}")
            val userMessage = Discussion(
                "",
                user!!.id!!,
                user!!.profilePic!!,
                fullName,
                userInput,
                Utils.getCurrentDate()
            )

            viewModel.addDiscussion(plantName, userMessage).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                                ivSendMessage.isEnabled = false
                            }
                        }
                        is Result.Success -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                etUserInputMessage.setText("")
                                ivSendMessage.isEnabled = true
                            }

                            discussionAdapter.apply {
                                list.add(userMessage)
                                notifyItemInserted(list.size - 1)
                                binding.rvDiscussion.scrollToPosition(list.size - 1)
                            }
                        }
                        is Result.Error -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                ivSendMessage.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addReply(plantName: String, discussionId: String) {
        Log.i(TAG, "addReply: $discussionId")
        val userInput = binding.etUserInputMessage.text.toString()
        val fullName = user!!.firstName + " " + user!!.lastName

        if (userInput.isNotEmpty()) {
            val reply = Discussion(
                "",
                user!!.id!!,
                user!!.profilePic!!,
                fullName,
                userInput,
                Utils.getCurrentDate(),
            )

            viewModel.addReply(plantName, discussionId, reply).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                                ivSendMessage.isEnabled = false
                            }
                        }
                        is Result.Success -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                etUserInputMessage.setText("")
                                ivSendMessage.isEnabled = true
                                layoutReply.visibility = View.GONE
                                replyState = false
                            }
                        }
                        is Result.Error -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                ivSendMessage.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "DiscussionActivity"
        const val PLANT_NAME = "plant_name"
    }
}