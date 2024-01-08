package com.example.madcamp_week2

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class ReviewAdapter(private var reviewList: ArrayList<ReviewItem>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>()
{
    interface OnItemClickListener {
        fun onItemclick(position: Int){}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.review_title.text = reviewList[position].title
        holder.review_contnt.text = reviewList[position].content
//        val layoutparams = holder.itemView.layoutParams
//        layoutparams.height=5
//        holder.itemView.requestLayout()
    }

    override fun getItemCount(): Int {
        return reviewList.count()
    }

    inner class ReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val review_title = itemView.findViewById<TextView>(R.id.review_title)
        val review_contnt = itemView.findViewById<TextView>(R.id.review_content)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemclick(adapterPosition)
            }
        }

    }
    fun updateList(newList: ArrayList<ReviewItem>) {
        reviewList = newList
        notifyDataSetChanged()
    }

}