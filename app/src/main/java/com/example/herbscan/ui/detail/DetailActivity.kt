package com.example.herbscan.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.SectionPagerAdapter
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.ActivityDetailBinding
import com.example.herbscan.ui.discussion.DiscussionActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.example.herbscan.data.network.Result

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel>() {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var plantHome: Plant
    private var buttonState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        plantHome = intent.getParcelableExtra<Plant>(EXTRA_PLANT)!!

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvPlantName.text = plantHome.name
            Glide.with(this@DetailActivity)
                .load(plantHome.picture)
                .into(ivPlant)
            btnLookDiscussion.setOnClickListener {
                val intent = Intent(this@DetailActivity, DiscussionActivity::class.java)
                intent.putExtra(DiscussionActivity.PLANT_NAME, plantHome.name)
                startActivity(intent)
            }
        }

        setViewPager()
        getCurrentPlant(plantHome.name)
        getManyDiscussion(plantHome.name)
    }

    override fun onResume() {
        super.onResume()
        getManyDiscussion(plantHome.name)
    }

    private fun setViewPager() {
        val sectionPagerAdapter = SectionPagerAdapter(this)
        binding.viewPager.adapter = sectionPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, positon ->
            tab.text = resources.getString(TAB_TITLES[positon])
        }.attach()
    }

    private fun getCurrentPlant(plantName: String) {
        viewModel.getCurrentPlant(plantName).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> { }
                    is Result.Success -> {
                        val plantId = result.data
                        Log.i(TAG, "getCurrentPlant: $plantId")
                        checkFavoritePlant(plantId)
                    }
                    is Result.Error -> { }
                }
            }
        }
    }

    private fun checkFavoritePlant(plantId: String) {
        Log.i(TAG, "checkFavoritePlant: $plantId")
        viewModel.checkFavoritePlant(plantId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            ivFav.isEnabled = true
                        }
                    }
                    is Result.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            ivFav.isEnabled = true

                            Log.i(TAG, "checkFavoritePlant: ${result.data}")

                            if (result.data == "ada") {
                                ivFav.setImageResource(R.drawable.ic_fav)
                                buttonState = true
                            } else {
                                ivFav.setImageResource(R.drawable.ic_unfav)
                                buttonState = false
                            }

                            Log.i(TAG, "checkFavoritePlant: $buttonState")
                            binding.ivFav.setOnClickListener {
                                if (!buttonState) {
                                    Log.i(TAG, "checkFavoritePlant: ")
                                    addFavoritePlant(plantId)
                                } else {
                                    deleteFavoritePlant(plantId)
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            ivFav.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun getManyDiscussion(plantName: String) {
        viewModel.getManyDiscussion(plantName).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                        }
                    }
                    is Result.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            if (result.data == 0) {
                                tvDiscussion.text = getString(R.string.no_discussion)
                            } else {
                                tvDiscussion.text = getString(R.string.many_discussion, result.data)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun addFavoritePlant(plantId: String) {
        Log.i(TAG, "addFavoritePlant: $plantId")
        viewModel.addFavoritePlant(plantId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            ivFav.isEnabled = false
                        }
                    }
                    is Result.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            ivFav.setImageResource(R.drawable.ic_fav)
                            ivFav.isEnabled = true
                        }

                        buttonState = true
                    }
                    is Result.Error -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            ivFav.isEnabled = true
                        }

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun deleteFavoritePlant(plantId: String) {
        viewModel.deleteFavoritePlant(plantId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            ivFav.isEnabled = false
                        }
                    }

                    is Result.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            ivFav.isEnabled = true
                            ivFav.setImageResource(R.drawable.ic_unfav)
                        }

                        buttonState = false
                    }

                    is Result.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            ivFav.isEnabled = true
                        }

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "DetailActivity"
        const val EXTRA_PLANT = "extra_plant"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.biodata,
            R.string.description,
            R.string.rating
        )
    }
}