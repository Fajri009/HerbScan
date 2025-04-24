package com.example.herbscan.ui.home.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.RowPlantAdapter
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.ActivitySearchBinding
import com.example.herbscan.data.network.Result
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.ui.detail.tab.BiodataFragment
import com.example.herbscan.ui.detail.description.DescriptionFragment
import com.example.herbscan.ui.home.HomeViewModel
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var rowPlantAdapter: RowPlantAdapter
    private var plantList: ArrayList<Plant> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        getPlantByName("")
        searchPlant()
    }

    private fun searchPlant() {
        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i(TAG, "onTextChanged: $s")
                getPlantByName(s.toString())
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    private fun getPlantByName(name: String) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvSearch.layoutManager = layoutManager

        viewModel.getPlantByName(name).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> { }

                    is Result.Success -> {
                        plantList = result.data

                        binding.apply {
                            if (plantList.isEmpty()) {
                                layoutEmpty.visibility = View.VISIBLE
                                rvSearch.visibility = View.GONE
                            } else {
                                layoutEmpty.visibility = View.GONE
                                rvSearch.visibility = View.VISIBLE
                                rowPlantAdapter = RowPlantAdapter(plantList)
                                binding.rvSearch.adapter = rowPlantAdapter
                                rowPlantAdapter.setOnItemClickCallBack(object: RowPlantAdapter.OnItemClickCallBack{
                                    override fun onItemClicked(data: Plant) {
                                        showSelectedPlant(data)
                                    }
                                })
                            }
                        }
                    }

                    is Result.Error -> {}
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

    companion object {
        private const val TAG = "SearchActivity"
    }
}