package com.example.herbscan.ui.classify.result

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.databinding.ActivityResultBinding
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.data.network.firebase.PredictionResult
import com.example.herbscan.ui.classify.ClassifyViewModel

class ResultActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ClassifyViewModel> {
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

        val predictionResult = intent.getParcelableExtra<PredictionResult>(EXTRA_PLANT)
        Log.i(TAG, "onCreate: $predictionResult")
        getPredictionResult(predictionResult!!)

        binding.apply {
            ivBack.setOnClickListener { finish() }
            layoutGoToDetail.setOnClickListener {
                val intent = Intent(this@ResultActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_PLANT, plantResult)
                startActivity(intent)
            }
        }
    }

    private fun getPredictionResult(predictionResult: PredictionResult) {
        val namePlantPure = predictionResult.prediction.substringAfterLast("(").removeSuffix(")").trim()
        val scientificName = predictionResult.prediction.substringBeforeLast("(").trim()
        val accuracyNumber = predictionResult.probability

        val accuracyCheck = accuracyNumber.replace("%", "").replace(",", ".").toDouble()

        Log.i(TAG, "classifyImage: namePlant = $namePlantPure")
        Log.i(TAG, "getPredictionResult: probability = $accuracyNumber")
        Log.i(TAG, "getPredictionResult: accuracyCheck = $accuracyCheck")

        getPlantByName(namePlantPure)

        binding.apply {
            Glide.with(this@ResultActivity)
                .load(predictionResult.image)
                .into(ivPlant)
            tvPlantName.text = namePlantPure
            tvPlantScientificName.text = scientificName
            tvAccuracyNumber.text = accuracyNumber

            if (accuracyCheck >= 80) {
                tvAccuracyNumber.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.success_50))
                layoutResultSuccess.visibility = View.VISIBLE
                layoutResultFailed.visibility = View.GONE
            } else {
                tvAccuracyNumber.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.error_50))
                layoutResultSuccess.visibility = View.GONE
                layoutResultFailed.visibility = View.VISIBLE
            }
        }
    }

    private fun getPlantByName(namePlant: String) {
        viewModel.getPlantByName(namePlant).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        plantResult = result.data.first()

                        binding.apply {
                            tvPlantScientificName.text = plantResult.scientificName
                            if (plantResult.recommendation.isBlank()) {
                                layoutRecommend.visibility = View.GONE
                            } else {
                                tvPlantName.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                    topMargin = resources.getDimensionPixelSize(R.dimen.margin_10dp)
                                }
                                tvRecommend.visibility = View.VISIBLE
                                tvRecommend.text = getString(R.string.recommendation_for, plantResult.recommendation)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ResultActivity"
        const val EXTRA_PLANT = "extra_plant"
    }
}