package com.example.madcamp_week2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.example.madcamp_week2.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        binding.button1.setOnClickListener{
            signIn()
        }
        binding.button2.setOnClickListener{
            signOut()
        }
        binding.button3.setOnClickListener{
            GetCurrentUserProfile()
        }
        return binding.root
    }
    private fun setResultSignUp(){
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                if(result.resultCode== Activity.RESULT_OK){
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account=completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            Log.d("로그인한 유저의 이메일", email)
        }catch (e: ApiException){
            Log.w("failed", "failed code =" + e.statusCode)
        }
    }
    private fun signIn(){
        val signIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signIntent)
    }
    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                // ...
            }
    }
    private fun GetCurrentUserProfile() {
        val curUser = GoogleSignIn.getLastSignedInAccount(requireContext())
        curUser?.let {
            val email = curUser.email.toString()

            Log.d("현재 로그인 되어있는 유저의 이메일", email)
        }
    }

}