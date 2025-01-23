package com.example.herbscan.ui.camera.result

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.data.local.room.HistoryEntity
import com.example.herbscan.databinding.ActivityResultBinding
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Plant

class ResultActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var plantResult: Plant
    private var userModel = User()
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        val plantHistory = intent.getParcelableExtra<HistoryEntity>(EXTRA_PLANT)
        Log.i(TAG, "onCreate: $plantHistory")
        val namePlantPure = plantHistory!!.plantName.substringAfterLast("(").removeSuffix(")").trim()
        val fromPage = intent.getStringExtra(FROM_PAGE)
        Log.i(TAG, "classifyImage: namePlant = $namePlantPure")

        binding.apply {
            ivBack.setOnClickListener { finish() }
            ivPlant.setImageURI(plantHistory.image.toUri())
            tvPercentage.text = plantHistory.accuracy
            layoutGoToDetail.setOnClickListener {
                val intent = Intent(this@ResultActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_PLANT, plantResult)
                startActivity(intent)
            }

            getPlantByName(plantHistory.image, namePlantPure, userModel.uid!!, plantHistory.date, plantHistory.accuracy, fromPage!!)

            val percentageText = tvPercentage.text.toString()
            val percentageString = percentageText.replace("[^0-9]".toRegex(), "")
            val percentage = percentageString.toIntOrNull() ?: 0

            if (percentage >= 75) {
                if (percentage >= 100) {
                    tvPercentage.setText(R.string.one_hundred_percent)
                }
                tvPercentage.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.success_50))
            } else {
                tvPercentage.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.error_500))
            }
        }
    }

    private fun getPlantByName(
        imagePlant: String,
        namePlant: String,
        uid: String,
        date: String,
        probability: String,
        fromPage: String
    ) {
        viewModel.getPlantByName(namePlant).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        plantResult = result.data.first()

                        binding.apply {
                            tvPlantName.text = plantResult.name
                            tvPlantScientificName.text = plantResult.scientificName
                        }

                        val history = HistoryEntity(
                            image = imagePlant,
                            userId = uid,
                            date = date,
                            plantName = plantResult.name,
                            plantScientificName = plantResult.scientificName,
                            accuracy = probability
                        )

                        Log.i(TAG, "getPlantByName: fromPage = $fromPage")
                        if (fromPage == "CameraFragment") {
                            viewModel.insertHistory(history)
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ResultActivity"
        const val EXTRA_PLANT = "extra_plant"
        const val FROM_PAGE = "from_page"
    }
}