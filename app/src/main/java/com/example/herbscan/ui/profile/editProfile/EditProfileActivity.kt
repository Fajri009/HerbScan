package com.example.herbscan.ui.profile.editProfile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager.*
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.herbscan.MainActivity
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.databinding.ActivityEditProfileBinding
import com.example.herbscan.databinding.PopUpChangesBinding
import com.example.herbscan.ui.profile.editProfile.bottomsheet.EditProfileBottomSheetFragment
import com.example.herbscan.ui.profile.editProfile.bottomsheet.OnImageSelectedListener
import com.example.herbscan.data.network.Result
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity(), OnImageSelectedListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userAuth: UserAuth
    private val viewModel by viewModels<EditProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        userAuth = intent.getParcelableExtra("USER_AUTH")!!

        binding.apply {
            ivBack.setOnClickListener { finish() }
            ivEditProfile.setOnClickListener { showBottomSheet() }
            Glide
                .with(this@EditProfileActivity)
                .load(userAuth.profile_pic)
                .into(ivProfilePicture)
            etFirstName.setText(userAuth.first_name)
            etLastName.setText(userAuth.last_name)
            etPhoneNumber.setText(userAuth.phone_number)
            etEmail.setText(userAuth.email)
            btnSave.setOnClickListener { saveChanges() }
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = EditProfileBottomSheetFragment()
        bottomSheetDialog.setOnImageSelectedListener(this)
        bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
    }

    private fun saveChanges() {
        val profilePic = when (val drawable = binding.ivProfilePicture.drawable) {
            is BitmapDrawable -> {
                drawable.bitmap?.let { bitmap ->
                    getImageUri(this, bitmap)
                }
            }
            is VectorDrawable -> {
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                getImageUri(this, bitmap)
            }
            else -> {
                null
            }
        }
        val etFirstName = binding.etFirstName.text.toString()
        val etLastName = binding.etLastName.text.toString()
        val etPhoneNumber = binding.etPhoneNumber.text.toString()
        val etEmail = binding.etEmail.text.toString()

        val user = hashMapOf(
            "first_name" to etFirstName,
            "last_name" to etLastName,
            "email" to etEmail,
            "phone_number" to etPhoneNumber
        )

        viewModel.updateCurrentUser(profilePic!!, user).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        showPopUp()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showPopUp() {
        val popupBinding = PopUpChangesBinding.inflate(layoutInflater)
        popupBinding.tvChangesSuccess.text = getString(R.string.changes_success)
        val alertDialog = AlertDialog.Builder(this)
            .setView(popupBinding.root)
            .setCancelable(false)
            .create()

        val window = alertDialog.window
        window?.setBackgroundDrawableResource(R.drawable.pop_up_background)
        val layoutParams = window?.attributes
        layoutParams?.width = LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        alertDialog.show()

        popupBinding.btnOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateToProfile", true)
            startActivity(intent)
            alertDialog.dismiss()
        }
    }

    override fun onImageSelected(imageUri: Uri) {
        binding.ivProfilePicture.setImageURI(imageUri)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}