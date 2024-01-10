package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class postpost(
    val success: String
)
interface postpostapi{
    @GET("/getwritername")
    fun getwritername(
        @Query("postid") postid: String): Call<postpost>
}

private lateinit var mretrofit: Retrofit
private lateinit var mRetrofitAPI: postpostapi
var writername = ""
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
        val date1 = view.findViewById<TextView>(R.id.detailDate1)
        val date2 = view.findViewById<TextView>(R.id.detailDate2)

        val loc = view.findViewById<TextView>(R.id.detailLoc)
        val content = view.findViewById<TextView>(R.id.detailContent)
        val chatBtn = view.findViewById<Button>(R.id.moveToChatBtn)

        title.text = item?.title
        date1.text = item?.date1
        date2.text = item?.date2
        loc.text = item?.location
        content.text = item?.content

        Glide.with(this)
            .load(item?.imgfilename) // imageUrl은 PostItem에서 실제 이미지 URL을 가져오는 메서드 또는 속성으로 변경 필요
            .into(image)

        mretrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(postpostapi::class.java)
        val call: Call<postpost> = mRetrofitAPI.getwritername(item?.writerid.toString())
        call.enqueue(object: Callback<postpost> {
            override fun onResponse(call: Call<postpost>, response: Response<postpost>) {
                if(response.isSuccessful){
                    writername = response.body()?.success ?: ""
                    writer.text = writername
                }
            }
            override fun onFailure(call: Call<postpost>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        chatBtn.setOnClickListener {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra("channel_name", item?.title)
            intent.putExtra("others_id", item?.writerid)
            intent.putExtra("IsFromPost", "yes")
            startActivity(intent)
        }

        return view
    }

}