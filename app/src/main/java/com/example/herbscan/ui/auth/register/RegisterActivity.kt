package com.example.herbscan.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.databinding.ActivityRegisterBinding
import com.example.herbscan.ui.auth.login.LoginActivity
import com.example.herbscan.utils.Utils
import com.example.herbscan.data.network.Result

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            tvLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btnRegister.setOnClickListener {
                register()
            }
        }
    }

    private fun register() {
        val etFirstName = binding.etRegisterFirstName.text.toString()
        val etLastName = binding.etRegisterLastName.text.toString()
        val etEmail = binding.etRegisterEmail.text.toString()
        val etPhoneNumber = binding.etRegisterPhoneNumber.text.toString()
        val etPassword = binding.etRegisterPassword.text.toString()
        val etConfirmPassword = binding.etRegisterConfirmPassword.text.toString()

        if (etFirstName.isEmpty() || etLastName.isEmpty() || etEmail.isEmpty() || etPhoneNumber.isEmpty() || etPassword.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (!Utils.isValidEmail(etEmail) || etPassword.length < 8 || etPassword.length > 8) {
            showToast(getString(R.string.invalid_form))
        } else if (etPassword != etConfirmPassword) {
            showToast(getString(R.string.pass_not_match))
        } else {
            val auth = hashMapOf(
                "email" to etEmail,
                "password" to etPassword
            )

            val user = hashMapOf(
                "nama depan" to etFirstName,
                "nama belakang" to etLastName,
                "email" to etEmail,
                "nomor telepon" to etPhoneNumber,
            )

            viewModel.register(auth, user).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(getString(R.string.try_login))

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            if (result.error == "Failed to register : The email address is already in use by another account.") {
                                showToast(getString(R.string.email_registered))
                            } else {
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}