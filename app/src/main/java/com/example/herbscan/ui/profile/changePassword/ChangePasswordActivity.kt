package com.example.herbscan.ui.profile.changePassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.herbscan.MainActivity
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.network.firebase.UserAuth
import com.example.herbscan.databinding.ActivityChangePasswordBinding
import com.example.herbscan.databinding.PopUpChangesBinding
import com.example.herbscan.data.network.Result

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var userAuth: UserAuth
    private val viewModel by viewModels<ChangePasswordViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userAuth = intent.getParcelableExtra("USER_AUTH")!!
        binding.etEmail.setText(userAuth.email)

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            btnSend.setOnClickListener {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        val etEmail = binding.etEmail.text.toString()

        if (etEmail.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else {
            viewModel.changePassword(etEmail).observe(this@ChangePasswordActivity) { result ->
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
    }

    private fun showPopUp() {
        val popupBinding = PopUpChangesBinding.inflate(layoutInflater)
        popupBinding.tvChangesSuccess.text = getString(R.string.changes_success)
        val alertDialog = AlertDialog.Builder(this)
            .setView(popupBinding.root)
            .setCancelable(false)
            .create()
        alertDialog.show()

        popupBinding.btnOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateToProfile", true)
            startActivity(intent)
            alertDialog.dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_SHORT).show()
    }
}