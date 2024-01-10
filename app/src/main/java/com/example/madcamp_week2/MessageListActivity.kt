package com.example.madcamp_week2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.madcamp_week2.databinding.ActivityMessageListBinding
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory
import okhttp3.ResponseBody
import org.bson.types.ObjectId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//import io.getstream.chatapplication.databinding.ActivityMessageListBinding

data class check_value(
    val value: Int
)
interface valueapi{
    @GET("/checkteam")
    fun checkteam(
        @Query("myid") myid: ObjectId,
        @Query("otheruserid") userid: ObjectId): Call<check_value>
}
var otheruserid: String =""
var myid: String = ""
private lateinit var mretrofit: Retrofit
private lateinit var mRetrofitAPI: valueapi

class AppAlertDialog(
    private val context: Context,
    private val title: String? = null,
    private val msg: String?= null
) {
    private lateinit var positiveListener: () -> Unit
    private lateinit var negativeListener: () -> Unit
    private val dialog: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton("확인") { _, _ ->
                if (::positiveListener.isInitialized) positiveListener()
            }
            .setNegativeButton("취소") { _, _ ->
                if (::negativeListener.isInitialized) negativeListener()
            }.setNeutralButton("") { _, _ ->

            }
    }

    fun show(onClickNegative: () -> Unit = {}, onClickPositive: () -> Unit = {}) {
        this.negativeListener = onClickNegative
        this.positiveListener = onClickPositive
        dialog.show().apply {
            /**
             * Title TextView
             */
            findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.apply {
                textSize = 18f
                setTextColor(context.getColor(R.color.main))
            }
            /**
             * Message TextView
             */
            findViewById<TextView>(android.R.id.message)?.apply {
                textSize = 13f
                setTextColor(context.getColor(R.color.black))
            }

            /**
             * Positive Button
             */
            findViewById<TextView>(android.R.id.button1)?.apply {
                textSize = 18f
                setTextColor(context.getColor(R.color.main))
            }
            /**
             * Negative Button
             */
            findViewById<TextView>(android.R.id.button2)?.apply {
                textSize = 18f
                setTextColor(context.getColor(R.color.gray))
            }
    }
}}

class MessageListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - ViewBinding 초기화
        binding = ActivityMessageListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //chatactivity에서 넘어온 유저아이디
        if (intent.hasExtra("others_id")) {
            println("작동2" + intent.getStringExtra("others_id"))
            otheruserid = intent.getStringExtra("others_id")?:""
            myid = intent.getStringExtra("my_id").toString()?:""
        }

        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "MessageListActivity를 시작하기 위해서는 채널 아이디 (cid) 정보가 필요합니다."
        }

        // Step 1 - 채팅방 컴포넌트에 연결할 뷰모델 초기화
        val factory = MessageListViewModelFactory(cid)
        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        // Step 2 - 채팅방 컴포넌트에 뷰모델 연결
        messageListHeaderViewModel.bindView(binding.messageListHeaderView, this)
        messageListViewModel.bindView(binding.messageListView, this)
        messageInputViewModel.bindView(binding.messageInputView, this)

        // Step 3 - MessageListHeaderView와 MessageInputView 연결 및 채팅 쓰레드 모드 업데이트
        messageListViewModel.mode.observe(this) { mode ->
            when (mode) {
                is MessageListViewModel.Mode.Thread -> {
                    messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                    messageInputViewModel.setActiveThread(mode.parentMessage)
                }
                MessageListViewModel.Mode.Normal -> {
                    messageListHeaderViewModel.resetThread()
                    messageInputViewModel.resetThread()
                }
            }
        }

        // Step 4 - 채팅방 동작에 대한 이벤트 구독 및 NavigateUp 이벤트 발생 시 채팅방 종료
        messageListViewModel.state.observe(this) { state ->
            if (state is MessageListViewModel.State.NavigateUp) {
                finish()
            }
        }

        // Step 5 - 채팅방 헤더의 뒤로가기 버튼 및 디바이스 백버튼 터치 시 NavigateUp 이벤트 송출
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.messageListHeaderView.setBackButtonClickListener(backHandler)
        onBackPressedDispatcher.addCallback(this) {
            backHandler()
        }

        //현재 채팅중인 유저가 나의 team 리스트에 있으면
        //GET /user
        // result.team.include(상대방id) == true
        var test =0
        mretrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(valueapi::class.java)

        val sendid = ObjectId(myid)
        val sendotherid = ObjectId(otheruserid)
        val call: Call<check_value> = mRetrofitAPI.checkteam(sendid, sendotherid)
        call.enqueue(object: Callback<check_value> {
            override fun onResponse(call: Call<check_value>, response: Response<check_value>) {
                if(response.isSuccessful){
                    val output = response.body()
                    println("output is " + output?.value)
                    if(output?.value == 1){
                        test = 1
                    }
                }
            }

            override fun onFailure(call: Call<check_value>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        //showReviewDialog()
        binding.messageListHeaderView.setAvatarClickListener {
            if (test == 1)
                showReviewDialog()
            else
                showConfirmDialog()
        }
    }

    private fun showConfirmDialog() {
        AppAlertDialog(this, "동행을 확정하시겠습니까?", "동행을 확정하시려면 확인 버튼을 눌러주세요.").show(
            onClickPositive = {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("returnToTab", "Chatting")
                intent.putExtra("currentState", "Confirm")
                intent.putExtra("otheruserid", otheruserid)
                intent.putExtra("myuserid", myid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            },
            onClickNegative = {}
        )
    }

    private fun showReviewDialog() {
        AppAlertDialog(this, "리뷰를 작성하시겠습니까?", "리뷰를 작성하시려면 확인 버튼을 눌러주세요.").show(
            onClickPositive = {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("returnToTab", "Chatting")
                intent.putExtra("currentState", "Review")
                intent.putExtra("otheruserid", otheruserid)
                intent.putExtra("myuserid", myid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            },
            onClickNegative = {}
        )

    }


    companion object {
        // MessageListActivity의 인텐트 생성 및 채팅방의 cid 정보 전달
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, MessageListActivity::class.java).putExtra(CID_KEY, channel.cid)
    }
}