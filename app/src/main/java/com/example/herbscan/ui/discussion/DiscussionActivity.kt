package com.example.herbscan.ui.discussion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.*
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.DiscussionAdapter
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.databinding.ActivityDiscussionBinding
import com.example.herbscan.utils.Utils
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.databinding.PopUpDiscussionBinding
import com.example.herbscan.ui.chat.ChatActivity

class DiscussionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscussionBinding
    private val viewModel by viewModels<DiscussionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var user: UserAuth? = null
    private lateinit var discussionAdapter: DiscussionAdapter
    private var plantName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        plantName = intent.getStringExtra(PLANT_NAME)!!

        getDiscussion(plantName)
        getCurrentUser()

        binding.apply {
            tvPlantName.text = getString(R.string.discussion_plant, plantName)
            ivBack.setOnClickListener { finish() }
            rvDiscussion.layoutManager = LinearLayoutManager(this@DiscussionActivity)
            fabAddDiscussion.setOnClickListener { showPopUp() }
        }
    }

    override fun onResume() {
        super.onResume()
        getDiscussion(plantName)
    }

    private fun getDiscussion(plantName: String) {
        viewModel.getDiscussion(plantName).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading ->  {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        val discussion = result.data
                        Log.i(TAG, "getDiscussion: $discussion")

                        binding.apply {
                            progressBar.visibility = View.GONE

                            if (discussion.isEmpty()) {
                                layoutEmpty.visibility = View.VISIBLE
                                rvDiscussion.visibility = View.GONE
                            } else {
                                layoutEmpty.visibility = View.GONE
                                rvDiscussion.visibility = View.VISIBLE
                                discussionAdapter = DiscussionAdapter(discussion)
                                binding.rvDiscussion.adapter = discussionAdapter
                                discussionAdapter.setOnItemClickCallBack(object: DiscussionAdapter.OnItemClickCallBack {
                                    override fun onItemClicked(data: Discussion) {
                                        val intent = Intent(this@DiscussionActivity, ChatActivity::class.java)
                                        intent.putExtra(ChatActivity.EXTRA_DISCUSSION, data)
                                        intent.putExtra(ChatActivity.PLANT_NAME, plantName)
                                        startActivity(intent)
                                    }
                                })
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

    private fun showPopUp() {
        val popupBinding = PopUpDiscussionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setView(popupBinding.root)
            .setCancelable(false)
            .create()

        alertDialog.show()

        popupBinding.apply {
            btnSubmit.setOnClickListener {
                val input = popupBinding.etTitleDiscussion.text.toString()
                addDiscussion(plantName, input)
                getDiscussion(plantName)
                alertDialog.dismiss()
            }
            ivClose.setOnClickListener { alertDialog.dismiss() }
        }
    }

    private fun addDiscussion(plantName: String, input: String) {
        val fullName = user!!.first_name + " " + user!!.last_name

        if (input.isNotEmpty()) {
            Log.i(TAG, "sendMessage: @${user!!.uid}")
            val discussion = Discussion(
                "",
                user!!.uid!!,
                user!!.profile_pic!!,
                fullName,
                input,
                Utils.getCurrentDate()
            )

            viewModel.addDiscussion(plantName, discussion).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                            }
                        }
                        is Result.Success -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                            }

                            discussionAdapter.apply {
                                list.add(discussion)
                                notifyItemInserted(list.size - 1)
                                binding.rvDiscussion.scrollToPosition(list.size - 1)
                            }
                        }
                        is Result.Error -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
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