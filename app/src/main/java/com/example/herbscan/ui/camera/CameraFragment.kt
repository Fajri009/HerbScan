package com.example.herbscan.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.databinding.FragmentCameraBinding
import com.example.herbscan.databinding.PopUpInfoBinding
import com.example.herbscan.ui.camera.result.ResultActivity
import com.example.herbscan.utils.Utils
import java.io.FileDescriptor
import java.io.IOException
import com.example.herbscan.data.network.Result
import com.example.herbscan.databinding.PopUpLoadingBinding

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.apply {
            ivCaptureCamera.setOnClickListener { takePhoto() }
            ivInfo.setOnClickListener { showInfo() }
            ivGalleryCamera.setOnClickListener { startGallery() }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
        binding.ivCaptureCamera.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewCamera.surfaceProvider
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showInfo() {
        val popUpBinding = PopUpInfoBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(popUpBinding.root)
            .setCancelable(false)
            .create()

        val window = alertDialog.window
        window?.setBackgroundDrawableResource(R.drawable.pop_up_background)
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        alertDialog.show()

        popUpBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        binding.ivCaptureCamera.isEnabled = false

        val photoFile = Utils.createCustomTempFile(requireContext())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        val popUpBinding = PopUpLoadingBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(popUpBinding.root)
            .setCancelable(false)
            .create()
        alertDialog.show()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val rotation = computeRelativeRotation()
                    val bitmap = savedUri.toBitmap()?.rotateBitmap(rotation.toFloat())
                    classifyImage(bitmap!!, alertDialog, savedUri)
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = uri.toBitmap()

            val popUpBinding = PopUpLoadingBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(popUpBinding.root)
                .setCancelable(false)
                .create()
            alertDialog.show()

            classifyImage(bitmap!!, alertDialog, uri)
        } else {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun classifyImage(image: Bitmap, alertDialog: AlertDialog, uri: Uri) {
        viewModel.classifyImage(image).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val plant = result.data

                        val intent = Intent(requireContext(), ResultActivity::class.java)
                        intent.putExtra(ResultActivity.IMAGE_PLANT, uri.toString())
                        intent.putExtra(ResultActivity.PLANT_NAME, plant.first)
                        intent.putExtra(ResultActivity.PROBABILITY, plant.second)
                        intent.putExtra(ResultActivity.DATE, Utils.getCurrentDate())
                        intent.putExtra(ResultActivity.FROM_PAGE, "CameraFragment")
                        alertDialog.dismiss()
                        startActivity(intent)

                    }

                    is Result.Error -> {}
                }
            }
        }
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }
                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture?.targetRotation = rotation
            }
        }
    }

    private fun Uri.toBitmap(): Bitmap? {
        try {
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(this, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }

    private fun computeRelativeRotation(): Int {
        val cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = getCameraId()
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val sensorOrientationDegrees = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        val sign = if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
            CameraCharacteristics.LENS_FACING_FRONT
        ) 1 else -1
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
        return (sensorOrientationDegrees - rotation * sign + 360) % 360
    }

    @androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
    private fun getCameraId(): String {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get()
        val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
        val camera2CameraInfo = Camera2CameraInfo.from(camera.cameraInfo)
        return camera2CameraInfo.cameraId
    }

    companion object {
        private const val TAG = "CameraFragment"
        const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}