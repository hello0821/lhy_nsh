package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import org.bson.types.ObjectId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface reviewapi{
    @POST("/addReview")
    fun addReview(
        @Query("otheruserid") otherid: ObjectId,
        @Query("myid") myid: ObjectId,
        @Query("review") review: String): Call<check_value>
}
class ReviewFragment: Fragment() {
    var mretrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var mRetrofitAPI = mretrofit.create(reviewapi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review, container, false)
        val submitBtn = view.findViewById<Button>(R.id.submitReview)

        submitBtn.setOnClickListener {
            val reviewText = view.findViewById<EditText>(R.id.reviewText).text.toString()
            addReview(reviewText)
            moveToPost()
        }

        return view
    }

    private fun addReview(review: String) {
        val sendotherid = (activity as MainActivity) ?.otheruserid ?: ""
        val sendmyid = (activity as MainActivity) ?.myuserid ?: ""

        val otherid = ObjectId(sendotherid)
        val myid = ObjectId(sendmyid)
        val addReview: Call<check_value> = mRetrofitAPI.addReview(otherid, myid, review)
        addReview.enqueue(object: Callback<check_value> {
            override fun onResponse(call: Call<check_value>, response: Response<check_value>) {
                if(response.isSuccessful){
                    val responsedata = response.body()
                    responsedata?.let{
                        Log.d("1", "successful")
                    }
                }
                else{
                    println("response문제")
                }
            }
            override fun onFailure(call: Call<check_value>, t: Throwable) {
                Log.e("WritePost", "onFailure", t)
            }
        })


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