package com.example.herbscan.ui.rating

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Rating
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.databinding.ActivityRatingBinding
import com.example.herbscan.ui.discussion.DiscussionActivity.Companion.TAG
import com.example.herbscan.ui.profile.editProfile.bottomsheet.OnImageSelectedListener
import com.example.herbscan.utils.Utils

class RatingActivity : AppCompatActivity(), OnImageSelectedListener {
    private lateinit var binding: ActivityRatingBinding
    private lateinit var photoAdapter: PhotoAdapter
    private val selectedPhotos = mutableListOf<Uri>()
    private var ratingValue: Int = 0
    private var plantName: String = ""
    private var user: UserAuth? = null
    private val viewModel by viewModels<RatingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        plantName = intent.getStringExtra(PLANT_NAME)!!
        Log.i(TAG, "onCreate: $plantName")

        setupRecyclerView()
        getCurrentUser()

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            etCamera.setOnClickListener {
                if (selectedPhotos.size < MAX_PHOTOS) {
                    showBottomSheet()
                } else {
                    Toast.makeText(
                        this@RatingActivity,
                        "Maksimal $MAX_PHOTOS foto yang dapat ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(selectedPhotos) { position ->
            removePhoto(position)
        }

        binding.rvPhotos.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(
                this@RatingActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        user = result.data
                        Log.i(TAG, "getCurrentUser: $user")

                        addRating(plantName)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RatingActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addRating(plantName: String) {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                ratingValue = rating.toInt()
            }
        }

        binding.btnSend.setOnClickListener {
            val imageUris = mutableListOf<Uri>()
            for (i in 0 until 5) {
                imageUris.add(
                    if (i < selectedPhotos.size) selectedPhotos[i]
                    else Uri.EMPTY
                )
            }

            val etRating = binding.etAddRating.text.toString()

            val rating = Rating(
                "",
                user!!.uid!!,
                user!!.first_name + " " + user!!.last_name,
                user!!.profile_pic!!,
                ratingValue,
                etRating,
                "",
                "",
                "",
                "",
                "",
                Utils.getCurrentDate()
            )

            if (ratingValue == 0 || etRating.isEmpty()) {
                showToast(getString(R.string.empty_form))
            } else {
                binding.btnSend.isEnabled = false

                Log.i(TAG, "imageUri: $imageUris")
                viewModel.addRating(plantName, rating, imageUris).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                finish()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this@RatingActivity, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = RatingBottomSheet()
        bottomSheetDialog.setOnImageSelectedListener(this)
        bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
    }

    override fun onImageSelected(imageUri: Uri) {
        addPhoto(imageUri)
    }

    private fun addPhoto(uri: Uri) {
        photoAdapter.addPhoto(uri)
        updatePhotosVisibility()
    }

    private fun removePhoto(position: Int) {
        photoAdapter.removePhoto(position)
        updatePhotosVisibility()
    }

    private fun updatePhotosVisibility() {
        binding.rvPhotos.visibility = if (selectedPhotos.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RatingActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val MAX_PHOTOS = 5
        const val PLANT_NAME = "plant_name"
    }
}