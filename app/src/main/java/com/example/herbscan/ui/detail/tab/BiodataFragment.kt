package com.example.herbscan.ui.detail.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.FragmentBiodataBinding

class BiodataFragment : Fragment() {
    private lateinit var binding: FragmentBiodataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBiodataBinding.inflate(inflater, container, false)

        val plant = requireActivity().intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        binding.apply {
            tvScientificName.text = plant!!.scientificName
            tvAvailability.text = plant.availability
            tvHabitat.text = plant.habitat

            if (plant.availability == "Umum") {
                tvAvailability.setTextColor(resources.getColor(R.color.success_900))
            }
        }

        return binding.root
    }

    companion object {
        const val EXTRA_PLANT = "extra_plant"
    }
}