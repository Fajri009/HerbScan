package com.example.herbscan.ui.auth.forgotpass

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.databinding.ActivityForgotPassBinding
import com.example.herbscan.ui.auth.login.LoginActivity
import com.example.herbscan.utils.Utils
import com.example.herbscan.data.network.Result

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPassBinding
    private val viewModel by viewModels<ForgotPassViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            tvLogin.setOnClickListener {
                val intent = Intent(this@ForgotPassActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btnSend.setOnClickListener {
                forgotPass()
            }
        }
    }

    private fun forgotPass() {
        val etEmail = binding.etForgotPassEmail.text.toString()

        if (etEmail.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (!Utils.isValidEmail(etEmail)) {
            showToast(getString(R.string.invalid_form))
        } else {
            viewModel.forgotPass(etEmail).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(getString(R.string.send_success))

                            val intent = Intent(this@ForgotPassActivity, LoginActivity::class.java)
                            startActivity(intent)
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

    private fun showToast(message: String) {
        Toast.makeText(this@ForgotPassActivity, message, Toast.LENGTH_SHORT).show()
    }
}