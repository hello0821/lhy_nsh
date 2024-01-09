package com.example.madcamp_week2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Chatting : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chatting, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        val currentState = mainActivity?.teamState

        when (currentState) {
            "Confirm" -> mainActivity?.loadConfirmFragment()
            "Review" -> mainActivity?.loadReviewFragment()
        }
    }

}