package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.madcamp_week2.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class id(
    val _id: String
)
interface signupapi {
    @POST("/signup")
    fun signup(@Body data: PostData): Call<id>
}

class SignUpActivity : AppCompatActivity() {

    var mretrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var mRetrofitAPI = mretrofit.create(signupapi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        binding.signup.setOnClickListener{
            val textname = binding.name.text
            val textnickname = binding.nickname.text
            val texttype = binding.type.text
            val textintroduction = binding.introduction.text
            val signupdata = PostData("12","2",textintroduction.toString(),textname.toString(),
                "1", listOf(Review("2","2")),listOf(2),"2", texttype.toString())
            sendtoserver(signupdata)

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(binding.root)
    }
    private fun sendtoserver(data: PostData){
        val postCall: Call<id> = mRetrofitAPI.signup(data)
        postCall.enqueue(object: Callback<id>{
            override fun onResponse(call: Call<id>, response: Response<id>) {
                if(response.isSuccessful){
                    println("yes!!!!!!")
                    val responsedata = response.body()
                    responsedata?.let{
                        Log.d("1", "user id is${it._id}")
                    }
                }
                else{
                    println("response문제")
                }
            }
            override fun onFailure(call: Call<id>, t: Throwable) {
                println("fail")
            }
        })
    }
}

