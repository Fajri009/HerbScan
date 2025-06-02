package com.example.herbscan.ui.classify

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.herbscan.R
import com.example.herbscan.data.local.preference.classification.ClassificationFeatures
import com.example.herbscan.data.local.preference.classification.Features
import com.example.herbscan.databinding.ActivitySettingClassificationBinding

class SettingClassificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingClassificationBinding

    private var featuresModel = Features()
    private lateinit var classificationFeatures: ClassificationFeatures

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        classificationFeatures = ClassificationFeatures(this)

        featuresModel = classificationFeatures.getFeatures()

        Log.i(TAG, "onCreate: ${featuresModel.clahe}")
        Log.i(TAG, "onCreate: ${featuresModel.colorConversion}")
        Log.i(TAG, "onCreate: ${featuresModel.dataAugmentation}")

        if (featuresModel.clahe) {
            binding.cbClahe.isChecked = true
        }

        if (featuresModel.colorConversion) {
            binding.cbColorConversion.isChecked = true
        }

        if (featuresModel.dataAugmentation) {
            binding.cbDataAugmentation.isChecked = true
        }

        binding.apply {
            ivBack.setOnClickListener { backPage() }
        }
    }

    private fun backPage() {
        if (binding.cbClahe.isChecked) {
            featuresModel.clahe = true
        } else {
            featuresModel.clahe = false
        }

        if (binding.cbColorConversion.isChecked) {
            featuresModel.colorConversion = true
        } else {
            featuresModel.colorConversion = false
        }

        if (binding.cbDataAugmentation.isChecked) {
            featuresModel.dataAugmentation = true
        } else {
            featuresModel.dataAugmentation = false
        }

        classificationFeatures.setFeatures(featuresModel)

        finish()
    }

    companion object {
        private const val TAG = "SettingClassificationActivity"
    }
}