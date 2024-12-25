package com.example.herbscan.di

import com.example.herbscan.data.HerbScanRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

object Injection {
    fun provideRepository(): HerbScanRepository {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDb = Firebase.database
        val firebaseStorage = FirebaseStorage.getInstance()
        return HerbScanRepository(firebaseAuth, firebaseDb, firebaseStorage)
    }
}