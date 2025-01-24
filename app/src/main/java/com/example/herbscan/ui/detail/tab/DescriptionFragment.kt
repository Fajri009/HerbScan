package com.example.herbscan.ui.detail.tab

import android.os.Bundle
import android.text.SpannableStringBuilder
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
            tvPlantBenefit.text = formatWithBullets(plant!!.description.benefit)
            tvPlantHowToUse.text= formatWithBullets(plant.description.howToUse)
            tvSideEffect.text = formatWithBullets(plant.description.sideEffect)

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

    companion object {
        const val EXTRA_PLANT = "extra_plant"
    }
}