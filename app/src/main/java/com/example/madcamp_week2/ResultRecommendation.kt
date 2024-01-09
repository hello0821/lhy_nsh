package com.example.madcamp_week2

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultRecommendation : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.result_recommendation, container, false)
        val recommendedCountry = arguments?.getString("recommendedCountry")

        val resultCountryText = view.findViewById<TextView>(R.id.resultCountry)
        val moveToPost = view.findViewById<Button>(R.id.moveToPostBtn)

        resultCountryText.text = recommendedCountry

        moveToPost.setOnClickListener {
            (activity as? MainActivity)?.moveToPostsFragment()
        }

        return view
    }
}