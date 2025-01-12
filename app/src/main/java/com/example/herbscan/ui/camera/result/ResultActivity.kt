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
import com.example.herbscan.databinding.ActivityResultBinding
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Plant

class ResultActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance()
    }
    private lateinit var plantResult: Plant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePlant = intent.getStringExtra(IMAGE_PLANT)
        val probability = intent.getStringExtra(PROBABILITY)
        val namePlant = intent.getStringExtra(PLANT_NAME)
        val namePlantPure = namePlant?.substringAfterLast("(")?.removeSuffix(")")?.trim()
        Log.i(TAG, "classifyImage: namePlant = $namePlantPure")

        binding.apply {
            ivBack.setOnClickListener { finish() }
            ivPlant.setImageURI(imagePlant!!.toUri())
            tvPercentage.text = probability
            layoutGoToDetail.setOnClickListener {
                val intent = Intent(this@ResultActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_PLANT, plantResult)
                startActivity(intent)
            }

            getPlantByName(namePlantPure!!)

//            tvPercentage.text = "$percetage%"
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

    private fun getPlantByName(namePlant: String) {
        viewModel.getIPlantByName(namePlant).observe(this) { result ->
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
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ResultActivity"
        const val IMAGE_PLANT = "image_plant"
        const val PLANT_NAME = "plant_name"
        const val PROBABILITY = "probability"
    }
}