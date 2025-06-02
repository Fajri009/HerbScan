package com.example.herbscan.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Category
import com.example.herbscan.data.network.firebase.Chat
import com.example.herbscan.data.network.firebase.Description
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.data.network.firebase.Favorite
import com.example.herbscan.data.network.firebase.History
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.data.network.firebase.PredictionResult
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.tflite.TFLiteHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HerbScanRepository(
    private val firebaseAuth: FirebaseAuth,
    db: FirebaseDatabase,
    storage: FirebaseStorage,
    private val tfLiteHelper: TFLiteHelper,
) {
    private val userRef = db.reference.child("users")
    private val categoryRef = db.reference.child("category")
    private val plantRef = db.reference.child("plant")
    private val descriptionRef = db.reference.child("description")
    private val predictionRef = db.reference.child("prediction")
    private val historyRef = db.reference.child("history")
    private val favoriteRef = db.reference.child("favorite")
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
                    userMap["profile_pic"] = downloadUrl.toString()

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

    fun getCurrentUser(): LiveData<Result<UserAuth, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val uid = currentUser.uid
                    val userSnapshot = userRef.child(uid).get().await()
                    Log.i(TAG, "getCurrentUser: $userSnapshot")
                    val dataUser = userSnapshot.getValue(UserAuth::class.java)
                    val user = dataUser?.copy(uid = uid)
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

//    fun getImagePlant(plantName: String): LiveData<Result<String, String>> =
//        liveData {
//            emit(Result.Loading)
//
//            try {
//                val imagePlant = storageRef.child("plant/$plantName.jpg")
//                val downloadUrl = imagePlant.downloadUrl.await()
//
//                val plantSnapshot = plantRef.get().await()
//
//                for (plantData in plantSnapshot.children) {
//                    val plant = plantData.getValue(Plant::class.java)
//                    if (plant != null && plant.name.contains(plantName, ignoreCase = true)) {
//                        val updatePlant = plant.copy(picture = downloadUrl.toString())
//                        plantData.ref.setValue(updatePlant).await()
//                        emit(Result.Success("Berhasil memperbarui gambar tanaman"))
//                    }
//                }
//
//                Log.i(TAG, "classifyImage ($plantName): $downloadUrl")
//            } catch (e: Exception) {
//                Log.e(TAG, "Failed to get image plant : ${e.message}")
//                emit(Result.Error("Failed to get image plant : ${e.message}"))
//            }
//        }

    fun getCurrentPlant(plantName: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val plantSnapshot = plantRef.get().await()

                for (plantData in plantSnapshot.children) {
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null && plant.name.contains(plantName, ignoreCase = true)) {
                        val plantId = plantData.key!!
                        Log.i(TAG, "getCurrentPlant: $plantId")
                        emit(Result.Success(plantId))
                    }
                }
                Log.i(TAG, "getCurrentPlant (plantName): $plantName")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get current plant : ${e.message}")
                emit(Result.Error("Failed to get current plant : ${e.message}"))
            }
        }

    fun addFavoritePlant(plantId: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favorite = favoriteRef.push()
                    val favoriteMap = Favorite(uid, plantId)

                    favorite.setValue(favoriteMap).await()
                    emit(Result.Success("Berhasil menambahkan tanaman ke favorite"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add favorite plant : ${e.message}")
                emit(Result.Error("Failed to add favorite plant : ${e.message}"))
            }
        }

    fun checkFavoritePlant(plantId: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                var category = ""

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favoriteSnapshot = favoriteRef.get().await()

                    for (favoriteData in favoriteSnapshot.children) {
                        val favorite = favoriteData.getValue(Favorite::class.java)
                        Log.i(TAG, "checkFavoritePlant: favorite: $favorite")
                        if (favorite != null) {
                            if (favorite.uid == uid && favorite.plant_id == plantId) {
                                category = "ada"
                                break
                            } else {
                                category = "tidak ada"
                            }
                        } else {
                            category = "kosong"
                        }
                    }
                }

                Log.i(TAG, "checkFavoritePlant: $category")
                emit(Result.Success(category))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get favorite plant : ${e.message}")
                emit(Result.Error("Failed to get favorite plant : ${e.message}"))
            }
        }

    fun deleteFavoritePlant(plantId: String): LiveData<Result<String, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    val uid = currentUser.uid
                    val favoriteRef = favoriteRef.get().await()

                    for (favoriteData in favoriteRef.children) {
                        val favorite = favoriteData.getValue(Favorite::class.java)
                        if (favorite != null) {
                            if (favorite.uid == uid && favorite.plant_id == plantId) {
                                Log.i(TAG, "deleteFavoritePlant: $favoriteData")
                                favoriteData.ref.removeValue().await()
                                emit(Result.Success("Berhasil menghapus tanaman dari favorite"))
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete favorite plant : ${e.message}")
                emit(Result.Error("Failed to delete favorite plant : ${e.message}"))
            }
        }

    fun getAllFavoritePlant(plantName: String): LiveData<Result<ArrayList<Plant>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val currentUser = firebaseAuth.currentUser
                val favoriteSnapshot = favoriteRef.get().await()
                val favoriteList = ArrayList<Favorite>()

                if (currentUser != null) {
                    val uid = currentUser.uid

                    for (favoriteData in favoriteSnapshot.children) {
                        val favoritePlant = favoriteData.getValue(Favorite::class.java)
                        if (favoritePlant != null) {
                            if (favoritePlant.uid == uid) {
                                favoriteList.add(favoritePlant)
                            }
                        }
                    }
                }

                Log.i(TAG, "getAllFavoritePlant: $favoriteList")

                val plantSnapshot = plantRef.get().await()
                val plantList = ArrayList<Plant>()

                for (plantData in plantSnapshot.children) {
                    val plantId = plantData.key!!
                    val plant = plantData.getValue(Plant::class.java)
                    if (plant != null && plantId in favoriteList.map { it.plant_id } && plant.name.contains(plantName, ignoreCase = true)) {
                        plantList.add(plant)
                    }
                }
                Log.i(TAG, "getAllFavoritePlant: $plantList")

                emit(Result.Success(plantList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get favorite plant : ${e.message}")
                emit(Result.Error("Failed to get favorite plant : ${e.message}"))
            }
        }

    fun getDescription(plantId: String): LiveData<Result<ArrayList<Description>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val description = ArrayList<Description>()
                val descriptionRef = descriptionRef.child(plantId).get().await()
                val descriptionData = descriptionRef.getValue(Description::class.java)
                
                description.add(descriptionData!!)
                Log.i(TAG, "getDescription: $description")
                emit(Result.Success(description))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get description : ${e.message}")
                emit(Result.Error("Failed to get description : ${e.message}"))
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

    fun classifyImage(
        image: Bitmap,
        clahe: Boolean,
        colorConversion: Boolean,
        dataAugmentation: Boolean
    ): LiveData<Result<Triple<String, String, String>, String>> =
        liveData {
            emit(Result.Loading)

            Log.i(TAG, "classifyImage: ${clahe}, ${colorConversion}, ${dataAugmentation}")

            try {
                if (clahe) {
                    // Aktifkan CLAHE dengan parameter default
                    tfLiteHelper.enableCLAHE(true)
                } else {
                    tfLiteHelper.disableCLAHE()
                }

                if (colorConversion) {
                    // Aktifkan HSV conversion
                    tfLiteHelper.enableHSVConversion(true)
                } else {
                    tfLiteHelper.disableHSVConversion()
                }

                if (dataAugmentation) {
                    // Aktifkan data augmentation
                    tfLiteHelper.enableDataAugmentation(
                        enable = true,
                        rotation = 0f,
                        hFlip = false,      // Hindari flip jika bisa mengubah karakteristik
                        vFlip = false,
                        brightness = 0.1f   // Brightness adjustment kecil
                    )
                } else {
                    tfLiteHelper.disableDataAugmentation()
                }

                val result = tfLiteHelper.classifyImage(image, clahe, colorConversion, dataAugmentation)

                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to classify image : ${e.message}")
                emit(Result.Error("Failed to classify image : ${e.message}"))
            }
        }

    fun addPredictionResult(result: PredictionResult, image: Uri, uid: String): LiveData<Result<PredictionResult, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val prediction = predictionRef.push()
                val predictionId = prediction.key!!

                val imageRef = storageRef.child("prediction/$predictionId")
                val uploadTask = imageRef.putFile(image)
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

                val predictionResult = result.copy(image = downloadUrl.toString())
                prediction.setValue(predictionResult).await()

                val history = historyRef.push()
                val historyMap = History(
                    uid = uid,
                    prediction_id = predictionId
                )
                Log.i(TAG, "addPredictionResult: $historyMap")
                history.setValue(historyMap).await()

                emit(Result.Success(predictionResult))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add prediction : ${e.message}")
                emit(Result.Error("Failed to add prediction : ${e.message}"))
            }
        }

    fun getHistory(userId: String, namePlant: String): LiveData<Result<ArrayList<PredictionResult>, String>> =
        liveData {
            emit(Result.Loading)

            try {
                val historySnapshot = historyRef.get().await()
                val historyList = ArrayList<History>()

                for (historyData in historySnapshot.children) {
                    val history = historyData.getValue(History::class.java)
                    if (history != null && history.uid == userId) {
                        historyList.add(history)
                    }
                }
                Log.i(TAG, "getAllHistory (historyList): $historyList")

                val predictionSnapshot = predictionRef.get().await()
                val predictionList = ArrayList<PredictionResult>()

                for (predictionData in predictionSnapshot.children) {
                    val predictionId = predictionData.key!!
                    val prediction = predictionData.getValue(PredictionResult::class.java)
                    if (prediction != null && predictionId in historyList.map { it.prediction_id } && prediction.prediction.contains(namePlant, ignoreCase = true)) {
                        predictionList.add(prediction)
                    }
                }
                Log.i(TAG, "getHistory (predictionList): $predictionList")

                emit(Result.Success(predictionList))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get history : ${e.message}")
                emit(Result.Error("Failed to get history : ${e.message}"))
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

                    userMap["profile_pic"] = downloadUrl.toString()
                    Log.i(TAG, "updateCurrentUser: $userMap")

                    userRef.child(uid).setValue(userMap).await()
                    emit(Result.Success("Berhasil memperbarui data"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update current user : ${e.message}")
                emit(Result.Error("Failed to update current user : ${e.message}"))
            }
        }

    companion object {
        const val TAG = "HerbScanRepository"
    }
}