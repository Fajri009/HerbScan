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

        // Atau jika ingin dengan parameter kustom:
        // tfLiteHelper.enableCLAHE(true, clipLimit = 4.0f, tilesX = 8, tilesY = 8)

        // Aktifkan HSV conversion
        tfLiteHelper.enableHSVConversion(true)

        return HerbScanRepository(firebaseAuth, firebaseDb, firebaseStorage, tfLiteHelper)
    }
}