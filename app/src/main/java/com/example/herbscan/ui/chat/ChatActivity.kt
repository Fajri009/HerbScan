package com.example.herbscan.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.ChatAdapter
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Chat
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.databinding.ActivityChatBinding
import com.example.herbscan.databinding.PopUpRemoveDiscussionBinding
import com.example.herbscan.ui.discussion.DiscussionActivity.Companion.TAG
import com.example.herbscan.utils.Utils

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val viewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var user: UserAuth? = null
    private lateinit var discussion: Discussion
    private lateinit var plantName: String
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        discussion = intent.getParcelableExtra<Discussion>(EXTRA_DISCUSSION)!!
        plantName = intent.getStringExtra(PLANT_NAME)!!

        getCurrentUser()
        getChat(plantName, discussion.id)

        binding.apply {
            ivBack.setOnClickListener { finish() }
            tvDiscussion.text = discussion.title
            rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)
            ivSendMessage.setOnClickListener {
                addChat(plantName, binding.etUserInputMessage.text.toString())
            }
        }
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

                        val fullName = user!!.firstName + " " + user!!.lastName

                        if (discussion.name == fullName) {
                            binding.ivRemove.apply {
                                visibility = View.VISIBLE
                                setOnClickListener { showPopUp() }
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getChat(plantName: String, discussionId: String) {
        viewModel.getChat(plantName, discussionId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        val chat = result.data

                        binding.apply {
                            progressBar.visibility = View.GONE

                            if (chat.isEmpty()) {
                                layoutEmpty.visibility = View.VISIBLE
                                rvChat.visibility = View.GONE
                            } else {
                                layoutEmpty.visibility = View.GONE
                                rvChat.visibility = View.VISIBLE
                                chatAdapter = ChatAdapter(chat)
                                rvChat.adapter = chatAdapter
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showPopUp() {
        val popUBinding = PopUpRemoveDiscussionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setView(popUBinding.root)
            .setCancelable(false)
            .create()

        alertDialog.show()

        popUBinding.apply {
            btnYes.setOnClickListener { deleteDiscussion(plantName, discussion.id) }
            btnNo.setOnClickListener { alertDialog.dismiss() }
        }
    }

    private fun addChat(plantName: String, input: String) {
        val fullName = user!!.firstName + " " + user!!.lastName

        if (input.isNotEmpty()) {
            val chat = Chat(
                "",
                user!!.id!!,
                user!!.profilePic!!,
                fullName,
                input,
                Utils.getCurrentDate()
            )

            viewModel.addChat(plantName, discussion.id, chat).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> { }
                        is Result.Success -> {
                            binding.etUserInputMessage.setText("")
                            getChat(plantName, discussion.id)
                        }
                        is Result.Error -> {
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun deleteDiscussion(plantName: String, discussionId: String) {
        viewModel.deleteDiscussion(plantName, discussionId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE


                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_DISCUSSION = "discussion"
        const val PLANT_NAME = "plant_name"
    }
}