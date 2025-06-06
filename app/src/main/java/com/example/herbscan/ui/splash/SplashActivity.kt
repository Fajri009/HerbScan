package com.example.herbscan.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.herbscan.MainActivity
import com.example.herbscan.R
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.databinding.ActivitySplashBinding
import com.example.herbscan.ui.auth.login.LoginActivity
import java.io.IOException

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private lateinit var userModel: User
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (userModel.rememberMe) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
            catch (e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Terjadi kesalahan saat mengakses server", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT: Long = 3000
    }
}