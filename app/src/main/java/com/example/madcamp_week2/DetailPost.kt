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
        /*val writer = view.findViewById<TextView>(R.id.detailWriter)
        val image = view.findViewById<ImageView>(R.id.detailImage)*/
        val date1 = view.findViewById<TextView>(R.id.detailDate1)
        val date2 = view.findViewById<TextView>(R.id.detailDate2)

        val loc = view.findViewById<TextView>(R.id.detailLoc)
        /*val content = view.findViewById<TextView>(R.id.detailContent)*/

        title.text = item?.title
       /* writer.text = item?.writerid*/
        date1.text = item?.date1
        date2.text = item?.date2
        loc.text = item?.location
        /*content.text = item?.content*/

        return view
    }

}