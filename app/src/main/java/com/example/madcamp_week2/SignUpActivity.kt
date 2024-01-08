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

interface signupapi {
    @POST("/signup")
    fun signup(@Body data: PostData): Call<PostData>
}

class SignUpActivity : AppCompatActivity() {

    var mretrofit = Retrofit.Builder()
        .baseUrl("http://143.248.191.68:5000/")
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

            val signupdata = PostData(12,"2",textintroduction.toString(),textname.toString(),
                "1", listOf(Review("2","2")),listOf(2),"2", texttype.toString())
            sendtoserver(signupdata)
        }

        binding.goback.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(binding.root)
    }
    private fun sendtoserver(data: PostData){
        val postCall: Call<PostData> = mRetrofitAPI.signup(data)
        postCall.enqueue(object: Callback<PostData>{
            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                if(response.isSuccessful){
                    println("yes!!!!!!")
                    Log.d("1", "${response.body()}")
                }
                else{
                    println("response문제")
                }
            }
            override fun onFailure(call: Call<PostData>, t: Throwable) {
                println("fail")
            }
        })
    }
}

