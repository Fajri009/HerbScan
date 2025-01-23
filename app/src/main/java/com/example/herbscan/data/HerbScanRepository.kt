package com.example.herbscan.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.herbscan.data.local.room.HistoryDao
import com.example.herbscan.data.local.room.HistoryEntity
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Category
import com.example.herbscan.data.network.firebase.Chat
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.tflite.TFLiteHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HerbScanRepository(
    private val firebaseAuth: FirebaseAuth,
    db: FirebaseDatabase,
    storage: FirebaseStorage,
    private val tfLiteHelper: TFLiteHelper,
    private val herbScanDao: HistoryDao
) {
    private val userRef = db.reference.child("users")
    private val categoryRef = db.reference.child("category")
    private val plantRef = db.reference.child("plant")
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

    fun getCategory(): LiveData<Result<ArrayList<Category>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val categorySnapshot = categoryRef.get().await()
                val categoryList = ArrayList<Category>()

                for (categoryData in categorySnapshot.children) {
                    val category = categoryData.getValue(Category::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }
                }

                emit(Result.Success(categoryList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get category : ${e.message}")
                emit(Result.Error("Failed to get category : ${e.message}"))
            }
        }

    fun getAllPlant(): LiveData<Result<ArrayList<Plant>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                val plantList = ArrayList<Plant>()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        plantList.add(plant)
                    }
                }

                Log.i(TAG, "getAllPlant (plantList): $plantList")
                emit(Result.Success(plantList))
            } catch (e: Exception) {
                Log .e(TAG, "Failed to get all plant : ${e.message}")
                emit(Result.Error("Failed to get all plant : ${e.message}"))
            }
        }

    fun getPlantByName(namePlant: String): LiveData<Result<ArrayList<Plant>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                val plantList = ArrayList<Plant>()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(namePlant, ignoreCase = true)) {
                            plantList.add(plant)
                        }
                    }
                }

                Log.i(TAG, "getPlantByName (plantList): $plantList")
                emit(Result.Success(plantList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get plant by name : ${e.message}")
                emit(Result.Error("Failed to get plant by name : ${e.message}"))
            }
        }

    fun getImagePlant(): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val imagePlant = storageRef.child("plant/nangka mini.jpg")

                val downloadUrl = imagePlant.downloadUrl.await()

                Log.i(TAG, "classifyImage: $downloadUrl")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get image plant : ${e.message}")
                emit(Result.Error("Failed to get image plant : ${e.message}"))
            }
        }

    fun addFavoritePlant(plant: Plant): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favoriteRef = userRef.child(uid).child("favorite").child(plant.name)

                    favoriteRef.setValue(plant).await()
                    emit(Result.Success("Berhasil menambahkan tanaman ke favorite"))
                } else {
                    emit(Result.Error("Anda belum login"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add favorite plant : ${e.message}")
                emit(Result.Error("Failed to add favorite plant : ${e.message}"))
            }
        }

    fun getFavoritePlant(namePlant: String): LiveData<Result<ArrayList<Plant>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                val favoriteList = ArrayList<Plant>()

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favoriteRef = userRef.child(uid).child("favorite")
                    val favoriteSnapshot = favoriteRef.get().await()

                    for (favorite in favoriteSnapshot.children) {
                        val plant = favorite.getValue(Plant::class.java)

                        if (favorite != null) {
                            if (plant!!.name.contains(namePlant, ignoreCase = true)) {
                                favoriteList.add(plant)
                            }
                        }
                    }

                    Log.i(TAG, "getFavoritePlant (favoriteList): $favoriteList")
                    emit(Result.Success(favoriteList))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get favorite plant : ${e.message}")
                emit(Result.Error("Failed to get favorite plant : ${e.message}"))
            }
        }

    fun deleteFavoritePlant(plantName: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favoriteRef = userRef.child(uid).child("favorite").child(plantName)

                    favoriteRef.removeValue().await()
                    emit(Result.Success("Berhasil menghapus tanaman dari favorite"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete favorite plant : ${e.message}")
                emit(Result.Error("Failed to delete favorite plant : ${e.message}"))
            }
        }

    fun addDiscussion(plantName: String, discussion: Discussion): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                var discussionRef: DatabaseReference? = null
                var updateDiscussion: Discussion = discussion

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(plantName, ignoreCase = true)) {
                            discussionRef = plantData.child("discussion").ref.push()
                            updateDiscussion = discussion.copy(id = discussionRef.key!!)

                            break
                        }
                    }
                }

                if (discussionRef != null) {
                    discussionRef.setValue(updateDiscussion).await()
                    emit(Result.Success("Berhasil menambahkan diskusi"))
                } else {
                    emit(Result.Error("Tanaman tidak ditemukan"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add discussion : ${e.message}")
                emit(Result.Error("Failed to add discussion : ${e.message}"))
            }
        }

    fun getManyDiscussion(plantName: String): LiveData<Result<Int, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                var discussionCount = 0

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(plantName, ignoreCase = true)) {
                            val discussionRef = plantData.child("discussion").ref
                            val discussionSnapshot = discussionRef.get().await()

                            discussionCount = discussionSnapshot.childrenCount.toInt()
                            break
                        }
                    }
                }

                Log.i(TAG, "getManyDiscussion: $discussionCount")
                emit(Result.Success(discussionCount))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get discussion count: ${e.message}")
                emit(Result.Error("Failed to get discussion count: ${e.message}"))
            }
        }

    fun getDiscussion(plantName: String): LiveData<Result<ArrayList<Discussion>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                val discussionList = ArrayList<Discussion>()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(plantName, ignoreCase = true)) {
                            val discussionRef = plantData.child("discussion").ref
                            val discussionSnapshot = discussionRef.get().await()

                            for (discussionData in discussionSnapshot.children) {
                                val discussion = discussionData.getValue(Discussion::class.java)
                                if (discussion != null) {
                                    discussionList.add(discussion)
                                }
                            }
                        }
                    }
                }

                Log.i(TAG, "getDiscussion (discussionList): $discussionList")
                emit(Result.Success(discussionList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get discussion : ${e.message}")
                emit(Result.Error("Failed to get discussion : ${e.message}"))
            }
        }

    fun deleteDiscussion(plantName: String, discussionId: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null && plant.name.contains(plantName, ignoreCase = true)) {
                        val discussionRef = plantData.child("discussion").child(discussionId).ref
                        discussionRef.removeValue().await()
                        emit(Result.Success("Berhasil menghapus diskusi"))
                    }
                }

                emit(Result.Error("Diskusi tidak ditemukan"))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete discussion : ${e.message}")
                emit(Result.Error("Failed to delete discussion : ${e.message}"))
            }
        }

    fun addChat(plantName: String, discussionId: String, chat: Chat): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                var chatRef: DatabaseReference? = null

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(plantName, ignoreCase = true)) {
                            chatRef = plantData.child("discussion").child(discussionId).child("chat").ref.push()
                            break
                        }
                    }
                }

                if (chatRef != null) {
                    chatRef.setValue(chat).await()
                    emit(Result.Success("Berhasil menambahkan balasan"))
                } else {
                    emit(Result.Error("Diskusi tidak ditemukan"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add reply : ${e.message}")
                emit(Result.Error("Failed to add reply : ${e.message}"))
            }
        }

    fun getChat(plantName: String, discussionId: String): LiveData<Result<ArrayList<Chat>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()
                val chatList = ArrayList<Chat>()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null) {
                        if (plant.name.contains(plantName, ignoreCase = true)) {
                            val chatRef = plantData.child("discussion").child(discussionId).child("chat").ref
                            val chatSnapshot = chatRef.get().await()

                            for (chatData in chatSnapshot.children) {
                                val chat = chatData.getValue(Chat::class.java)
                                if (chat != null) {
                                    chatList.add(chat)
                                }
                            }
                        }
                    }
                }

                Log.i(TAG, "getChat: $chatList")
                emit(Result.Success(chatList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get chat : ${e.message}")
                emit(Result.Error("Failed to get chat : ${e.message}"))
            }
        }

    fun classifyImage(image: Bitmap): LiveData<Result<Pair<String, String>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val result = tfLiteHelper.classifyImage(image)

                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to classify image : ${e.message}")
                emit(Result.Error("Failed to classify image : ${e.message}"))
            }
        }

    fun insertHistory(history: HistoryEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            herbScanDao.addHistory(history)
        }
    }

    fun getHistoryByName(userId: String, namePlant: String): LiveData<List<HistoryEntity>> =
        herbScanDao.getHistoryByName(userId, namePlant)

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