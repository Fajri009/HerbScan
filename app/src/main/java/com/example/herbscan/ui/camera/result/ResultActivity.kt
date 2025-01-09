package com.example.herbscan.ui.camera.result

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.herbscan.R
import com.example.herbscan.databinding.ActivityResultBinding
import com.example.herbscan.ui.detail.DetailActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plant = intent.getStringExtra(IMAGE_PLANT)
//        val percentage = intent.getIntExtra("PERCENTAGE_KEY", 0)

        binding.apply {
            ivBack.setOnClickListener { finish() }
            ivPlant.setImageURI(plant!!.toUri())
            layoutGoToDetail.setOnClickListener {
                val intent = Intent(this@ResultActivity, DetailActivity::class.java)
                startActivity(intent)
            }

//            tvPercentage.text = "$percetage%"
            val percentageText = tvPercentage.text.toString()
            val percentageString = percentageText.replace("[^0-9]".toRegex(), "")
            val percentage = percentageString.toIntOrNull() ?: 0

            if (percentage >= 75) {
                tvPercentage.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.success_50))
            } else {
                tvPercentage.setTextColor(ContextCompat.getColor(this@ResultActivity, R.color.error_500))
            }
        }
    }

    companion object {
        const val IMAGE_PLANT = "image_plant"
    }
}