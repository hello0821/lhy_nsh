package com.example.madcamp_week2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ConfirmFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_confirm, container, false)

        val confirmBtn = view.findViewById<Button>(R.id.confirmBtn)

        confirmBtn.setOnClickListener {
            moveToPost()

        }

        return view
    }

    private fun moveToPost() {
        val postFragment = Posts()
        val transaction = requireActivity().supportFragmentManager
        transaction.beginTransaction()
            .replace(R.id.confirm_container, postFragment)
            .addToBackStack(null)
            .commit()

        (activity as MainActivity)?.moveToTab(0)
    }

}