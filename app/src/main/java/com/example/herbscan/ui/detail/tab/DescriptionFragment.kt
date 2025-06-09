package com.example.herbscan.ui.detail.tab

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.herbscan.R
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.FragmentDescriptionBinding
import com.example.herbscan.ui.detail.DetailActivity.Companion.TAG
import com.example.herbscan.ui.detail.DetailViewModel

class DescriptionFragment : Fragment() {
    private lateinit var binding: FragmentDescriptionBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val plant = requireActivity().intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        getCurrentPlant(plant!!.name)

        binding.apply {
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

    private fun formatWithBullets(text: String): CharSequence {
        val bulletPoints = text.split("\\\\n").filter { it.isNotBlank() }
        val formattedText = SpannableStringBuilder()

        for (point in bulletPoints) {
            formattedText.append("\u2022 ") // Bullet character
            formattedText.append(point)
            formattedText.append("\n")
        }

        return formattedText
    }

    private fun getCurrentPlant(plantName: String) {
        viewModel.getCurrentPlant(plantName).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> { }
                    is Result.Success -> {
                        val plantId = result.data
                        Log.i(TAG, "onCreateView: $plantId")

                        getDescription(plantId)
                    }
                    is Result.Error -> { }
                }
            }
        }
    }

    private fun getDescription(plantId: String) {
        viewModel.getDescription(plantId).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val description = result.data.first()

                        binding.apply {
                            tvPlantBenefit.text = formatWithBullets(description.benefit)
                            tvPlantHowToUse.text= formatWithBullets(description.how_to_use)
                            tvSideEffect.text = formatWithBullets(description.side_effect)
                        }
                    }
                    is Result.Error -> {}
                }
            }
        }
    }

    companion object {
        const val EXTRA_PLANT = "extra_plant"
    }
}