package com.example.herbscan.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.UserAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HerbScanRepository(
    private val firebaseAuth: FirebaseAuth,
    db: FirebaseDatabase,
    storage: FirebaseStorage
) {
    private val userRef = db.reference.child("users")
    private val storageRef = storage.reference

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
                    userMap["id"] = uid
                    Log.i(TAG, "userMap: $userMap")

                    val avatarRef = storageRef.child("avatar/pic_avatar.png")

                    val downloadUrl = avatarRef.downloadUrl.await()
                    userMap["profilePic"] = downloadUrl.toString()

                    userRef.child(uid).setValue(userMap).await()
                    emit(Result.Success("Berhasil Mendaftarkan Akun"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register : ${e.message}")
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
                Log.e(TAG, "Failed to login : ${e.message}")
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
                Log.e(TAG, "Failed to send password reset email : ${e.message}")
                emit(Result.Error("Failed to send password reset email : ${e.message}"))
            }
        }
    
    fun getCurrentUser(): LiveData<Result<UserAuth, String>> =
        liveData {
            emit(Result.Loading)
            
            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val uid = currentUser.uid
                    val userSnapshot = userRef.child(uid).get().await()
                    val user = userSnapshot.getValue(UserAuth::class.java)
                    Log.i(TAG, "get current user data: $user")
                    if (user != null) {
                        emit(Result.Success(user))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get current user : ${e.message}")
                emit(Result.Error("Failed to get current user : ${e.message}"))
            }
        }

    fun updateCurrentUser(imageUri: Uri, userMap: HashMap<String, String>): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val uid = currentUser.uid
                    val userSnapshot = userRef.child(uid).get().await()
                    val user = userSnapshot.getValue(UserAuth::class.java)
                    Log.i(TAG, "update current user data: $user")

                    val imageRef = storageRef.child("avatar/${uid}")
                    val uploadTask = imageRef.putFile(imageUri)
                    val downloadUrl = suspendCoroutine { continuation ->
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    continuation.resumeWithException(it)
                                }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(task.result)
                            } else {
                                task.exception?.let {
                                    continuation.resumeWithException(it)
                                }
                            }
                        }
                    }

                    userMap["profilePic"] = downloadUrl.toString()
                    Log.i(TAG, "updateCurrentUser: $userMap")

                    userRef.child(uid).setValue(userMap).await()
                    emit(Result.Success("Berhasil memperbarui data"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update current user : ${e.message}")
                emit(Result.Error("Failed to update current user : ${e.message}"))
            }
        }

    fun changePassword(email: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                emit(Result.Success("Email telah dikirim ke email yang terdaftar"))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send password reset email : ${e.message}")
                emit(Result.Error("Failed to send password reset email : ${e.message}"))
            }
        }

    companion object {
        const val TAG = "HerbScanRepository"
    }
}