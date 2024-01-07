package com.example.madcamp_week2

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface mypageapi {
    @GET("/mypage")
    fun signup(@Header("Authorization") token:String): Call<PostData>
}
class Mypage : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mretrofit: Retrofit
    private lateinit var mRetrofitAPI: mypageapi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mypage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences 초기화
        sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        mretrofit = Retrofit.Builder()
            .baseUrl("http://143.248.191.68:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(mypageapi::class.java)
        // MyPageFragment에서 토큰 사용 예시
        val storedToken = getStoredToken()
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $storedToken"
        view.findViewById<View>(R.id.letsgo).setOnClickListener{
            getmypageinfo(storedToken)
        }
    }
    private fun getStoredToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }
    private fun getmypageinfo(token: String) {
        val call: Call<PostData> = mRetrofitAPI.signup(token)

        call.enqueue(object : Callback<PostData> {
            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                if (response.isSuccessful) {
                    // 성공적으로 사용자 정보를 가져왔을 때의 처리
                    val userInfo = response.body()
                    userInfo?.let{
                        Log.d("성공", "username은 ${it.name}")
                    }
                } else {
                    Log.e("fail", "Error message: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PostData>, t: Throwable) {
                println("실패")
            }

        })
    }
}