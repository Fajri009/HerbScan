package com.example.herbscan.di

import com.example.herbscan.data.repository.HerbScanRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

object Injection {
    fun provideRepository(): HerbScanRepository {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDb = Firebase.database
        return HerbScanRepository(firebaseAuth, firebaseDb)
    }
}