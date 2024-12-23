package com.example.herbscan

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.herbscan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var doubleBack = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBack) {
            finishAffinity()
            super.onBackPressed()
            return
        }

        doubleBack = true
        Toast.makeText(this@MainActivity, R.string.press_back_again, Toast.LENGTH_SHORT).show()

        handler.postDelayed({
            doubleBack = false
        }, 2000)
    }
}