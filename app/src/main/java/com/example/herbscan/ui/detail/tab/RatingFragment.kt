package com.example.herbscan.ui.detail.tab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.FragmentRatingBinding
import com.example.herbscan.ui.detail.DetailViewModel
import com.example.herbscan.ui.rating.RatingActivity

class RatingFragment : Fragment() {
    private lateinit var binding: FragmentRatingBinding
    private var plant: Plant? = null
    private lateinit var ratingAdapter: RatingAdapter
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRatingBinding.inflate(inflater, container, false)

        plant = requireActivity().intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()
        
        getAllRating(plant!!.name)

        binding.apply {
            rvRating.layoutManager = LinearLayoutManager(requireActivity())
            btnLookDiscussion.setOnClickListener {
                val intent = Intent(activity, RatingActivity::class.java)
                intent.putExtra(RatingActivity.PLANT_NAME, plant!!.name)
                Log.i(TAG, "onCreateView: ${plant!!.name}")
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllRating(plant!!.name)
    }

    private fun getAllRating(plantName: String) {
        viewModel.getAllRating(plantName).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val rating = result.data

                        Log.i(TAG, "getAllRating: ${userModel.uid}")

                        val hasUserRated = userModel.uid?.let { uid ->
                            rating.any { it.uid == uid }
                        } ?: false

                        binding.apply {
                            if (rating.isEmpty()) {
                                rvRating.visibility = View.GONE
                                layoutAddRating.visibility = View.VISIBLE
                            } else {
                                rvRating.visibility = View.VISIBLE
                                ratingAdapter = RatingAdapter(rating.asReversed())
                                rvRating.adapter = ratingAdapter

                                if (hasUserRated) {
                                    layoutAddRating.visibility = View.GONE
                                } else {
                                    layoutAddRating.visibility = View.VISIBLE
                                }
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

    companion object {
        const val TAG = "RatingFragment"
        const val EXTRA_PLANT = "extra_plant"
    }
}