package com.example.herbscan.ui.detail.tab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herbscan.data.network.firebase.Plant
import com.example.herbscan.databinding.FragmentRatingBinding
import com.example.herbscan.ui.rating.RatingActivity

class RatingFragment : Fragment() {
    private lateinit var binding: FragmentRatingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRatingBinding.inflate(inflater, container, false)

        val plant = requireActivity().intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        binding.btnLookDiscussion.setOnClickListener {
            val intent = Intent(activity, RatingActivity::class.java)
            intent.putExtra(RatingActivity.PLANT_NAME, plant!!.name)
            Log.i(TAG, "onCreateView: ${plant.name}")
            startActivity(intent)
        }

        return binding.root
    }

    companion object {
        const val TAG = "RatingFragment"
        const val EXTRA_PLANT = "extra_plant"
    }
}