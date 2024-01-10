package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface teamapi{
    @GET("/user/{userid}")
    suspend fun getUser(
        @Path("userid") userid: ObjectId): Response<PostData>

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

    lateinit var sendotherid: String
    lateinit var sendmyid: String
    var myName: String = ""
    var otherName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendotherid = (activity as MainActivity) ?.otheruserid ?: ""
        sendmyid = (activity as MainActivity)?.myuserid ?: ""

        lifecycleScope.launch {
            val getUserInfoDeferred = async { getUserName(ObjectId(sendmyid), "MY") }
            getUserInfoDeferred.await() // getUserInfo 함수가 완료될 때까지 기다립니다.

            val getUserInfoDeferred2 = async { getUserName(ObjectId(sendotherid), "OTHER") }
            getUserInfoDeferred2.await()

            println("1 Myname " + myName)
            println("1 OtherNAme " + otherName)

            setUpUI()

        }

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

    private fun setUpUI() {
        val user1 = view?.findViewById<TextView>(R.id.textOfFirstUser)
        val user2 = view?.findViewById<TextView>(R.id.textOfSecondUser)

        user1?.text = myName
        user2?.text = otherName

        println("2 Myname " + myName)
        println("2 OtherNAme " + otherName)
    }
    private suspend fun getUserName(id: ObjectId, identifier: String) {
        try {
            val response = mRetrofitAPI.getUser(id)
            if (response.isSuccessful) {
                val userInfo = response.body()
                val userName = userInfo?.name

                if (identifier == "MY") {
                    myName = userName?:""
                } else if (identifier == "OTHER") {
                    otherName = userName?:""
                }
            } else {
                Log.e("fail get user info", "Error message: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.e("getUserInfo", "Error: ${e.localizedMessage}")
        }

    }

    private fun addTeamMember() {
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