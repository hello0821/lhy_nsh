package com.example.madcamp_week2

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class MyData(
    val id: String
)

data class PostData(
    val _id: Int,
    val identityuri: String,
    val introd: String,
    val name: String,
    val profileuri: String,
    val reviews: List<Review>,
    val teamid: List<Int>,
    val temp: String,
    val type: String
)
data class Review(
    val description: String,
    val writer: String
)
data class check_result(
    val token: String
)
data class check_input(
    val name: String
)
interface ApiService {
    @GET("/")
    fun getData(): Call<PostData>
    @POST("/postdata")
    fun postData(@Body data: PostData): Call<check_result>

    @POST("/checkdata")
    fun checkdata(@Body data: check_input): Call<check_result>
}

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var mretrofit: Retrofit
    lateinit var mRetrofitAPI: ApiService

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Not Yet", Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            //.requestIdToken(getString(R.string.google_login_client_id))
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        with(binding) {
            button4.setOnClickListener {
                signIn()
            }

            button5.setOnClickListener {
                signOut()
            }
            button6.setOnClickListener {
                GetCurrentUserProfile()
            }
            button7.setOnClickListener{
                signup()
            }
        }

        setContentView(binding.root)
    }
    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // 정상적으로 결과가 받아와진다면 조건문 실행
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()
            val photoUrl = account?.photoUrl.toString()
            Log.d("로그인한 유저의 이메일", email)
            Log.d("로그인한 유저의 성", familyName)
            Log.d("로그인한 유저의 이름", givenName)
            Log.d("로그인한 유저의 전체이름", displayName)
            Log.d("로그인한 유저의 프로필 사진의 주소", photoUrl)

            mretrofit = Retrofit.Builder()
                .baseUrl("http://143.248.191.68:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            mRetrofitAPI = mretrofit.create(ApiService::class.java)
//            val call: Call<PostData> = mRetrofitAPI.getData()
//            call.enqueue(object: Callback<PostData>{
//                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
//                    if(response.isSuccessful){
//                        val data: PostData? = response.body()
//                        Log.d("서버값", "잘 들어옴, ${data?._id}")
//                    }
//                }
//                override fun onFailure(call: Call<PostData>, t: Throwable) {
//                    Log.e("fail", "why??: ${t.message}")
//                }
//            })
            val check_input_data = check_input(displayName)
            val postCall: Call<check_result> = mRetrofitAPI.checkdata(check_input_data)
            postCall.enqueue(object : Callback<check_result>{
                override fun onResponse(call: Call<check_result>, response: Response<check_result>){
                    if (response.isSuccessful){
                        val serverResponse = response.body()
                        if(serverResponse?.token=="1"){
                            moveTosignupActivity()
                        }
                        else{
                            println("yes!")
                            if (serverResponse != null) {
                                saveTokenToPreferences(serverResponse.token)
                                println("${serverResponse.token}")
                            }
                            moveTomainActivity()
                        }
                    }
                    else{
                        Log.e("fail", "Error message: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<check_result>, t: Throwable) {
                    Log.e("실패다실패", "요청실패: ${t.message}")
                }
            })
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }
    private fun signup(){
        moveTosignupActivity()
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                // ...
            }
    }

    private fun GetCurrentUserProfile() {
        val curUser = GoogleSignIn.getLastSignedInAccount(this)
        curUser?.let {
            val email = curUser.email.toString()
            val familyName = curUser.familyName.toString()
            val givenName = curUser.givenName.toString()
            val displayName = curUser.displayName.toString()
            val photoUrl = curUser.photoUrl.toString()

            Log.d("현재 로그인 되어있는 유저의 이메일", email)
            Log.d("현재 로그인 되어있는 유저의 성", familyName)
            Log.d("현재 로그인 되어있는 유저의 이름", givenName)
            Log.d("현재 로그인 되어있는 유저의 전체이름", displayName)
            Log.d("현재 로그인 되어있는 유저의 프로필 사진의 주소", photoUrl)
        }
    }

    private fun moveTomainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    private fun moveTosignupActivity(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun saveTokenToPreferences(token: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }
}