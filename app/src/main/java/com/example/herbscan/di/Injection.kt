package com.example.herbscan.di

import android.content.Context
import com.example.herbscan.data.HerbScanRepository
import com.example.herbscan.tflite.TFLiteHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

object Injection {
    fun provideRepository(context: Context): HerbScanRepository {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDb = Firebase.database
        val firebaseStorage = FirebaseStorage.getInstance()
        val tfLiteHelper = TFLiteHelper(context)

        // Aktifkan CLAHE dengan parameter default
        tfLiteHelper.enableCLAHE(true)

        // Aktifkan HSV conversion
        tfLiteHelper.enableHSVConversion(true)

        // Aktifkan data augmentation
        tfLiteHelper.enableDataAugmentation(
            enable = true,
            hFlip = true,         // Flip horizontal
            brightness = 0.1f     // Tingkatkan kecerahan sebesar 10%
        )

        return HerbScanRepository(firebaseAuth, firebaseDb, firebaseStorage, tfLiteHelper)
    }
}