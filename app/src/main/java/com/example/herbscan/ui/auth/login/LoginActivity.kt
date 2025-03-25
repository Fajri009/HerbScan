package com.example.herbscan.ui.auth.login

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
import com.example.herbscan.MainActivity
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.databinding.ActivityLoginBinding
import com.example.herbscan.ui.auth.forgotpass.ForgotPassActivity
import com.example.herbscan.ui.auth.register.RegisterActivity
import com.example.herbscan.utils.Utils
import com.example.herbscan.data.network.Result

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var doubleBack = false
    private val handler = Handler(Looper.getMainLooper())

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        userPreference = UserPreference(this)

        binding.apply {
            tvForgotPass.setOnClickListener {
                val intent = Intent(this@LoginActivity, ForgotPassActivity::class.java)
                startActivity(intent)
            }
            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
            btnLogin.setOnClickListener {
                login()
            }
        }
    }

    private fun login() {
        val etEmail = binding.etLoginEmail.text.toString()
        val etPassword = binding.etLoginPassword.text.toString()

        if (etEmail.isEmpty() || etPassword.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (!Utils.isValidEmail(etEmail) || etPassword.length < 8) {
            showToast(getString(R.string.invalid_form))
        } else {
            viewModel.login(
                etEmail,
                etPassword
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE

                            val response = result.data
                            userModel.uid = response

                            if (binding.cbRememberMe.isChecked) {
                                userModel.rememberMe = true
                            }

                            userPreference.setUser(userModel)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            if (result.error == "Failed to login : The supplied auth credential is incorrect, malformed or has expired.") {
                                showToast(getString(R.string.incorrect_login))
                            } else {
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBack) {
            finishAffinity()
            super.onBackPressed()
            return
        }

        doubleBack = true
        Toast.makeText(this@LoginActivity, R.string.press_back_again, Toast.LENGTH_SHORT).show()

        handler.postDelayed({
            doubleBack = false
        }, 2000)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}