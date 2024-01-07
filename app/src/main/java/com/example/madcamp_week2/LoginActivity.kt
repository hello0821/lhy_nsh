package com.example.madcamp_week2

import android.app.Activity
import android.content.Intent
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
import com.google.gson.Gson
import com.google.gson.JsonObject


class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

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
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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


            moveTomainActivity()
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
}