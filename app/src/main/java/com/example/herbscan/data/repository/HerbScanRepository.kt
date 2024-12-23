package com.example.herbscan.data.repository

import android.util.Log
import androidx.lifecycle.*
import com.example.herbscan.data.network.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class HerbScanRepository(
    private val firebaseAuth: FirebaseAuth,
    db: FirebaseDatabase
) {
    private val userRef = db.reference.child("users")

    fun register(
        authMap: HashMap<String, String>,
        userMap: HashMap<String, String>
    ): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(authMap["email"]!!, authMap["password"]!!).await()
                val user = authResult.user
                val uid = user?.uid

                if (uid != null) {
                    userRef.child(uid).setValue(userMap).await()
                    emit(Result.Success("Berhasil Mendaftarkan Akun"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register : ", e.cause)
                emit(Result.Error("Failed to register : ${e.message}"))
            }
        }

    fun login(
        email: String,
        password: String
    ): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val currentUser = firebaseAuth.currentUser
                val uid = currentUser?.uid

                if (uid != null) {
                    emit(Result.Success(uid))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to login : ", e.cause)
                emit(Result.Error("Failed to login : ${e.message}"))
            }
        }

    fun forgotPass(email: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                emit(Result.Success("Email telah dikirim ke email yang terdaftar"))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send password reset email : ", e.cause)
                emit(Result.Error("Failed to send password reset email : ${e.message}"))
            }
        }

    companion object {
        const val TAG = "HerbScanRepository"
    }
}