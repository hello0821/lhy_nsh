package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.bson.types.ObjectId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface teamapi{
    @POST("/addTeam")
    fun addTeam(
        @Query("myid") myid: ObjectId,
        @Query("otheruserid") otherid: ObjectId): Call<check_value>
}
class ConfirmFragment : Fragment() {
    var mretrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var mRetrofitAPI = mretrofit.create(teamapi::class.java)

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
            addTeamMember()
            moveToPost()
        }

        return view
    }

    private fun addTeamMember() {
        val sendotherid = (activity as MainActivity) ?.otheruserid ?: ""
        val sendmyid = (activity as MainActivity)?.myuserid ?: ""

        val otherid = ObjectId(sendotherid)
        val myid = ObjectId(sendmyid)

        val addTeam: Call<check_value> = mRetrofitAPI.addTeam(otherid, myid)
        addTeam.enqueue(object: Callback<check_value> {
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