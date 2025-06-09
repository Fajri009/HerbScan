package com.example.herbscan.ui.rating

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.R
import com.example.herbscan.databinding.ActivityRatingBinding
import com.example.herbscan.ui.profile.editProfile.bottomsheet.OnImageSelectedListener

class RatingActivity : AppCompatActivity(), OnImageSelectedListener {
    private lateinit var binding: ActivityRatingBinding
    private lateinit var photoAdapter: PhotoAdapter
    private val selectedPhotos = mutableListOf<Uri>()

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

        setupRecyclerView()
        setupClickListeners()
        rating()
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

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.etCamera.setOnClickListener {
            if (selectedPhotos.size < MAX_PHOTOS) {
                showBottomSheet()
            } else {
                Toast.makeText(
                    this,
                    "Maksimal $MAX_PHOTOS foto yang dapat ditambahkan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun rating() {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->

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

    companion object {
        private const val MAX_PHOTOS = 5
    }
}