package com.example.herbscan.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.adapter.SectionPagerAdapter
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.ActivityDetailBinding
import com.example.herbscan.ui.discussion.DiscussionActivity
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plantHome = intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvPlantName.text = plantHome!!.name
            Glide.with(this@DetailActivity)
                .load(plantHome.picture)
                .into(ivPlant)
            btnLookDiscussion.setOnClickListener {
                val intent = Intent(this@DetailActivity, DiscussionActivity::class.java)
                startActivity(intent)
            }
        }

        setViewPager()
    }

    private fun setViewPager() {
        val sectionPagerAdapter = SectionPagerAdapter(this)
        binding.viewPager.adapter = sectionPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, positon ->
            tab.text = resources.getString(TAB_TITLES[positon])
        }.attach()
    }

    companion object {
        const val EXTRA_PLANT = "extra_plant"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.biodata,
            R.string.description
        )
    }
}