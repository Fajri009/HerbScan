package com.example.herbscan.ui.discussion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.herbscan.databinding.ActivityDiscussionBinding

class DiscussionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscussionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}