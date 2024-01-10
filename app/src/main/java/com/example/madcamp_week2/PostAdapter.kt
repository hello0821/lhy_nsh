package com.example.madcamp_week2

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class check_url(
    val success: String
)
interface postadapterapi{
    @GET("/getpostimage")
    fun getpostimage(
        @Query("postid") postid: String): Call<check_url>
}

private lateinit var mretrofit: Retrofit
private lateinit var mRetrofitAPI: postadapterapi
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
        Glide.with(holder.itemView.context)
            .load(postList[position].imgfilename) // imageUrl은 PostItem에서 실제 이미지 URL을 가져오는 메서드 또는 속성으로 변경 필요
            .into(holder.post_img)
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
        val post_img = itemView.findViewById<ImageView>(R.id.postimage)

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