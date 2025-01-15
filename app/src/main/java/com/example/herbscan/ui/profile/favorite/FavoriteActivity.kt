package com.example.herbscan.ui.profile.favorite

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.herbscan.data.network.Result
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.RowPlantAdapter
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.ActivityFavoriteBinding
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.ui.detail.tab.BiodataFragment
import com.example.herbscan.ui.detail.tab.DescriptionFragment

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private val viewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var favoriteAdapter: RowPlantAdapter
    private var favoriteList: ArrayList<Plant> = arrayListOf()

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvFavorite.setHasFixedSize(true)
            rvFavorite.layoutManager = layoutManager
            ivBack.setOnClickListener { finish() }
        }

        getFavorite("")
        searchPlant()
    }

    override fun onResume() {
        super.onResume()
        getFavorite("")
        searchPlant()
    }

    private fun searchPlant() {
        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val query = s?.toString() ?: ""
                getFavorite(query)
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    private fun getFavorite(name: String) {
        viewModel.getAllFavorites(name).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        favoriteList = result.data

                        favoriteAdapter = RowPlantAdapter(favoriteList)
                        binding.rvFavorite.adapter = favoriteAdapter
                        favoriteAdapter.setOnItemClickCallBack(object: RowPlantAdapter.OnItemClickCallBack {
                            override fun onItemClicked(data: Plant) {
                                showSelectedPlant(data)
                            }
                        })
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showSelectedPlant(data: Plant) {
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra(DetailActivity.EXTRA_PLANT, data)
        startActivity(detailIntent)

        val biodataIntent = Intent(this, BiodataFragment::class.java)
        biodataIntent.putExtra(BiodataFragment.EXTRA_PLANT, data)

        val descriptionIntent = Intent(this, DescriptionFragment::class.java)
        descriptionIntent.putExtra(DescriptionFragment.EXTRA_PLANT, data)
    }
}