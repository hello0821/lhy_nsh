package com.example.madcamp_week2

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class PostAdapter(private var postList: ArrayList<PostItem>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>()
{
    interface OnItemClickListener {
        fun onItemclick(position: Int){}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.post_title.text = postList[position].title
        holder.post_date1.text = postList[position].date1
        holder.post_date2.text = postList[position].date2
        holder.post_loc.text = postList[position].location
    }

    override fun getItemCount(): Int {
        println(postList.count())
        return postList.count()
    }

    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val post_title = itemView.findViewById<TextView>(R.id.postTitle)
        val post_date1 = itemView.findViewById<TextView>(R.id.postDate1)
        val post_date2 = itemView.findViewById<TextView>(R.id.postDate2)
        val post_loc = itemView.findViewById<TextView>(R.id.postLoc)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemclick(adapterPosition)
            }
        }

    }
    fun updateList(newList: ArrayList<PostItem>) {
        postList = newList
        notifyDataSetChanged()
    }

}