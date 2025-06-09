package com.example.herbscan.ui.rating

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.herbscan.databinding.FragmentRatingBottomSheetBinding
import com.example.herbscan.ui.profile.editProfile.bottomsheet.OnImageSelectedListener
import com.example.herbscan.ui.profile.editProfile.camera.CameraProfileActivity
import com.example.herbscan.ui.profile.editProfile.camera.CameraProfileActivity.Companion.CAMERAX_RESULT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRatingBottomSheetBinding
    private var onImageSelectedListener: OnImageSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRatingBottomSheetBinding.inflate(inflater, container, false)

        dialog?.setCanceledOnTouchOutside(false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.apply {
            ivClose.setOnClickListener { dismiss() }
            layoutTakePhoto.setOnClickListener { checkCameraPermissionAndStartCameraX() }
            layoutChooseGallery.setOnClickListener { startGallery() }
        }

        return binding.root
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkCameraPermissionAndStartCameraX() {
        if (allPermissionsGranted()) {
            val intent = Intent(requireContext(), CameraProfileActivity::class.java)
            launcherIntentCameraX.launch(intent)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            val imageUri = it.data?.getStringExtra(CameraProfileActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            if (imageUri != null) {
                onImageSelectedListener?.onImageSelected(imageUri)
                dismiss()
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelectedListener?.onImageSelected(uri)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnImageSelectedListener(listener: OnImageSelectedListener) {
        onImageSelectedListener = listener
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}