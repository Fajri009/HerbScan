package com.example.herbscan.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.adapter.CategoryAdapter
import com.example.herbscan.adapter.ColumnPlantAdapter
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.databinding.FragmentHomeBinding
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Category
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.ui.detail.DetailActivity
import com.example.herbscan.ui.detail.tab.BiodataFragment
import com.example.herbscan.ui.detail.tab.DescriptionFragment
import com.example.herbscan.ui.home.search.SearchActivity
import java.util.ArrayList

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var columnPlantAdapter: ColumnPlantAdapter
    private lateinit var columnCategoryAdapter: CategoryAdapter
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var selectedCategory: String = "Semua"

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    private var plantList: ArrayList<Plant> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()

        binding.ivSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        getCurrentUser()
        showListCategory()
        showListPlant()

        getPlantImage("miana")

        return binding.root
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val user = result.data

                        binding.apply {
                            tvUserName.text = buildString {
                                append(user.first_name)
                                append(" ")
                                append(user.last_name)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun getPlantImage(plantName: String) {
        viewModel.getImagePlant(plantName).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showListCategory() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategory.layoutManager = layoutManager

        viewModel.getCategory().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val categoryList = result.data
                        columnCategoryAdapter = CategoryAdapter(categoryList)
                        binding.rvCategory.adapter = columnCategoryAdapter

                        columnCategoryAdapter.setSelectedCategory(selectedCategory)
                        filterPlantsByCategory(selectedCategory)

                        columnCategoryAdapter.setOnItemClickCallBack(object: CategoryAdapter.OnItemClickCallBack {
                            override fun onItemClicked(data: Category) {
                                Log.i(TAG, "onItemClicked: ${data.name}")
                                selectedCategory = data.name
                                filterPlantsByCategory(data.name)
                            }
                        })
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showListPlant() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager4 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvItem1.layoutManager = layoutManager
        binding.rvItem2.layoutManager = layoutManager2
        binding.rvItem3.layoutManager = layoutManager3
        binding.rvItem4.layoutManager = layoutManager4

        viewModel.getAllPlant().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        plantList = result.data
                        filterPlantsByCategory(selectedCategory)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun filterPlantsByCategory(categoryName: String) {
        val filteredList = if (categoryName == "Semua") {
            plantList
        } else {
            plantList.filter { it.home.category.contains(categoryName, ignoreCase = true) }
        }
        updateRecyclerViews(ArrayList(filteredList))
    }

    private fun updateRecyclerViews(plantList: ArrayList<Plant>) {
        binding.apply {
            columnPlantAdapter = ColumnPlantAdapter(plantList)
            rvItem1.adapter = columnPlantAdapter
            columnPlantAdapter.setOnItemClickCallBack(object: ColumnPlantAdapter.OnItemClickCallBack {
                override fun onItemClicked(data: Plant) {
                    Log.i(TAG, "onItemClicked: $data")
                    showSelectedPlant(data)
                }
            })

            val daunList = plantList.filter { it.home.part.contains("daun", ignoreCase = true) }
            if (daunList.isEmpty()) {
                tvLeaf.visibility = View.GONE
                rvItem2.visibility = View.GONE
            } else {
                tvLeaf.visibility = View.VISIBLE
                rvItem2.visibility = View.VISIBLE
                val daunPlantAdapter = ColumnPlantAdapter(ArrayList(daunList))
                rvItem2.adapter = daunPlantAdapter
                daunPlantAdapter.setOnItemClickCallBack(object: ColumnPlantAdapter.OnItemClickCallBack {
                    override fun onItemClicked(data: Plant) {
                        Log.i(TAG, "onItemClicked: $data")
                        showSelectedPlant(data)
                    }
                })
            }

            val buahList = plantList.filter { it.home.part.contains("buah", ignoreCase = true) }
            if (buahList.isEmpty()) {
                tvFruit.visibility = View.GONE
                rvItem3.visibility = View.GONE
            } else {
                tvFruit.visibility = View.VISIBLE
                rvItem3.visibility = View.VISIBLE
                val buahPlantAdapter = ColumnPlantAdapter(ArrayList(buahList))
                rvItem3.adapter = buahPlantAdapter
                buahPlantAdapter.setOnItemClickCallBack(object: ColumnPlantAdapter.OnItemClickCallBack {
                    override fun onItemClicked(data: Plant) {
                        Log.i(TAG, "onItemClicked: $data")
                        showSelectedPlant(data)
                    }
                })
            }

            val akarList = plantList.filter { it.home.part.contains("akar", ignoreCase = true) }
            if (akarList.isEmpty()) {
                tvRoot.visibility = View.GONE
                rvItem4.visibility = View.GONE
            } else {
                tvRoot.visibility = View.VISIBLE
                rvItem4.visibility = View.VISIBLE
                val akarPlantAdapter = ColumnPlantAdapter(ArrayList(akarList))
                rvItem4.adapter = akarPlantAdapter
                akarPlantAdapter.setOnItemClickCallBack(object: ColumnPlantAdapter.OnItemClickCallBack {
                    override fun onItemClicked(data: Plant) {
                        Log.i(TAG, "onItemClicked: $data")
                        showSelectedPlant(data)
                    }
                })
            }
        }
    }

    private fun showSelectedPlant(data: Plant) {
        val detailIntent = Intent(requireContext(), DetailActivity::class.java)
        detailIntent.putExtra(DetailActivity.EXTRA_PLANT, data)
        startActivity(detailIntent)

        val biodataIntent = Intent(requireContext(), BiodataFragment::class.java)
        biodataIntent.putExtra(BiodataFragment.EXTRA_PLANT, data)

        val descriptionIntent = Intent(requireContext(), DescriptionFragment::class.java)
        descriptionIntent.putExtra(DescriptionFragment.EXTRA_PLANT, data)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}