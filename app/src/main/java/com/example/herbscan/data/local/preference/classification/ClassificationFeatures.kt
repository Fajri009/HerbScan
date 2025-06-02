package com.example.herbscan.data.local.preference.classification

import android.content.Context

class ClassificationFeatures(context: Context) {
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setFeatures(value: Features) {
        val editor = preference.edit()
        editor.apply {
            putBoolean(CLAHE, value.clahe)
            putBoolean(COLOR_CONVERSION, value.colorConversion)
            putBoolean(DATA_AUGMENTATION, value.dataAugmentation)
            apply()
        }
    }

    fun getFeatures(): Features {
        val features = Features()
        features.apply {
            clahe = preference.getBoolean(CLAHE, false)
            colorConversion = preference.getBoolean(COLOR_CONVERSION, false)
            dataAugmentation = preference.getBoolean(DATA_AUGMENTATION, false)
        }

        return features
    }

    companion object {
        private const val PREFS_NAME = "classification_features"
        private const val CLAHE = "clahe"
        private const val COLOR_CONVERSION = "color_conversion"
        private const val DATA_AUGMENTATION = "data_augmentation"
    }
}