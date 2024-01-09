package com.example.madcamp_week2

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madcamp_week2.databinding.ActivityChatBinding
import com.example.madcamp_week2.MessageListActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import kotlin.properties.Delegates

//import io.getstream.chatapplication.databinding.ActivityMainBinding

interface chatapi {
    @GET("/mypage")
    suspend fun signup(@Header("Authorization") token:String): Response<PostData>
}

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModelFactory: ChannelListViewModelFactory
    private lateinit var client: ChatClient
    private lateinit var user: User

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mretrofit: Retrofit
    private lateinit var mRetrofitAPI: chatapi

    private var USER_NAME: String = ""
    private var USER_ID: String = ""
    private lateinit var server_token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        mretrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(chatapi::class.java)

        server_token = getStoredToken()

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $server_token"

        lifecycleScope.launch {
            val getUserInfoDeferred = async { getUserInfo(server_token) }
            getUserInfoDeferred.await() // getUserInfo 함수가 완료될 때까지 기다립니다.

            setUpUI()
        }

    }

    private fun setUpUI() {
        viewModelFactory = ChannelListViewModelFactory()

        // Step 0 - ViewBinding 초기화
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Step 1 - 오프라인 메세지 로드 및 전송 등 오프라인 기능 초기화
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = io.getstream.chat.android.offline.plugin.configuration.Config(),
            appContext = applicationContext,
        )

        // Step 2 - ChatClient 초기화
        client = ChatClient.Builder("fe9wszkeesp5", applicationContext)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()

        // Step 3 - 유저 정보 초기화
        user = User(
            id = USER_ID.toString(),
            name = USER_NAME,
            image = "https://bit.ly/2TIt8NR"
        )

        val token: String = server_token

        if (client.getCurrentUser() == null) {
            client.connectUser( // 유저 로그인
                user = user,
                token = token
            ).enqueue { result ->
                // Step 4 - 새로운 그룹 (채널) 생성
                if (result.isSuccess) {
                    updateChannelList()
                }
            }
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            showCreateChannelDialog()
        }

        // Step 5 - ChannelListViewModel 생성 및 ChannelListView과 연동
        val viewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val channelListHeaderViewModel : ChannelListHeaderViewModel by viewModels()
        viewModel.bindView(binding.channelListView, this)
        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(MessageListActivity.newIntent(this, channel))
        }
    }

    private fun getStoredToken() : String {
        val return_Value = sharedPreferences.getString("token", "")
        if (return_Value != null)
            return return_Value
        else
            return ""
    }

    private suspend fun getUserInfo(token: String) {
        try {
            val response = mRetrofitAPI.signup(token)
            if (response.isSuccessful) {
                val userInfo = response.body()
                userInfo?.let {
                    USER_NAME = it.name
                    USER_ID = it._id
                }
            } else {
                Log.e("fail get user info", "Error message: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.e("getUserInfo", "Error: ${e.localizedMessage}")
        }
    }

    private fun updateChannelList() {
        val viewModel: ChannelListViewModel by viewModels { viewModelFactory }
        viewModel.bindView(binding.channelListView, this)
    }


    private fun showCreateChannelDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("새 채널 생성")
            .setView(editText)
            .setPositiveButton("생성") { dialog, _ ->
                val channelName = editText.text.toString()
                createNewChannel(channelName)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun createNewChannel(channelName: String) {
        val channelId = "channel_${System.currentTimeMillis()}"
        client.createChannel(
            channelType = "messaging",
            channelId = channelId,
            memberIds = listOf("10", user.id),
            extraData = mapOf("name" to channelName)
        ).enqueue {
            if (it.isSuccess) {
                updateChannelList()
            } else {
                // 에러 처리
            }
        }
    }
}