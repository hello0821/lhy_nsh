package com.example.madcamp_week2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text

class DetailPost : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.detail_post, container, false)
        val item = arguments?.getParcelable<PostItem>("detailPostItem")

        val title = view.findViewById<TextView>(R.id.detailTitle)
        val writer = view.findViewById<TextView>(R.id.detailWriter)
        val image = view.findViewById<ImageView>(R.id.detailImage)
        val date = view.findViewById<TextView>(R.id.detailDate)
        val loc = view.findViewById<TextView>(R.id.detailLoc)
        val content = view.findViewById<TextView>(R.id.detailContent)

        title.text = item?.title
        writer.text = item?.writer
        date.text = item?.date
        loc.text = item?.loc
        content.text = item?.content

        return view
    }

}