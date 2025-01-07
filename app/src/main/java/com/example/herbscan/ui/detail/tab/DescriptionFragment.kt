package com.example.herbscan.ui.detail.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.FragmentDescriptionBinding

class DescriptionFragment : Fragment() {
    private lateinit var binding: FragmentDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val plant = requireActivity().intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        binding.apply {
            tvPlantBenefit.text = plant!!.description.benefit.replace("\\\\n", "\n")
            tvPlantHowToUse.text = plant.description.howToUse.replace("\\\\n", "\n")
            tvSideEffect.text = plant.description.sideEffect.replace("\\\\n", "\n")

            if (plant.recommendation.isBlank()) {
                layoutRecommend.visibility = View.GONE
            } else {
                tvRecommend.text = getString(R.string.recommendation_for, plant.recommendation)

                val layoutParams = tvBenefit.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.margin_20dp)
                tvBenefit.layoutParams = layoutParams
            }
        }

        return binding.root
    }

    companion object {
        const val EXTRA_PLANT = "extra_plant"
    }
}