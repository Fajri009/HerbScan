package com.example.herbscan.ui.profile.history

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.HistoryAdapter
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.data.local.room.HistoryEntity
import com.example.herbscan.databinding.ActivityHistoryBinding
import com.example.herbscan.ui.camera.result.ResultActivity

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var historyAdapter: HistoryAdapter

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvHistory.layoutManager = layoutManager
            ivBack.setOnClickListener { finish() }
        }

        getHistory(userModel.uid!!, "")
        searchPlant()
    }

    private fun getHistory(uid: String, query: String) {
        viewModel.getHistoryByName(uid, query).observe(this) { result ->
            binding.apply {
                if (result.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                    rvHistory.visibility = View.GONE
                } else {
                    layoutEmpty.visibility = View.GONE
                    rvHistory.visibility = View.VISIBLE
                    historyAdapter = HistoryAdapter(result.toList())
                    binding.rvHistory.adapter = historyAdapter
                    historyAdapter.setOnItemClickCallBack(object: HistoryAdapter.OnItemClickCallBack {
                        override fun onItemClicked(data: HistoryEntity) {
                            val intent = Intent(this@HistoryActivity, ResultActivity::class.java)
                            intent.putExtra(ResultActivity.EXTRA_PLANT, data)
                            intent.putExtra(ResultActivity.FROM_PAGE, "HistoryActivity")
                            startActivity(intent)
                        }
                    })
                }
            }
        }
    }

    private fun searchPlant() {
        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i(TAG, "onTextChanged: $s")
                val query = s?.toString() ?: ""
                getHistory(userModel.uid!!, query)
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    companion object {
        private const val TAG = "HistoryActivity"
    }
}