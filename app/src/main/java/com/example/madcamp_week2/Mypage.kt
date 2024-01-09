package com.example.madcamp_week2

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.net.URI

interface mypageapi {
    @GET("/mypage")
    fun signup(@Header("Authorization") token:String): Call<PostData>
    @POST("/imagetoserver")
    fun imagetoserver(@Header("Authorization") token:String, @Body uri: String): Call<PostData>
}
class ItemSpacingDecoration(private val spaceWidth: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = spaceWidth // 오른쪽 간격을 설정합니다.
    }
}
class Mypage : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mretrofit: Retrofit
    private lateinit var mRetrofitAPI: mypageapi
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var rv_list1: RecyclerView
    private val item_list1 = ArrayList<ReviewItem>()
    private val fullItemList1 = ArrayList<ReviewItem>()
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var imageLink: Uri
    private lateinit var storedToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mypage, container, false)
        rv_list1 = view.findViewById(R.id.rv_review)
//        fullItemList1.add(ReviewItem("예시 제목","예시 내용"))
//        fullItemList1.add(ReviewItem("예시 제목2","예시 내용2"))
//
//        item_list1.addAll(fullItemList1)
        reviewAdapter = ReviewAdapter(item_list1)
        rv_list1.adapter = reviewAdapter
        rv_list1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv_list1.addItemDecoration(ItemSpacingDecoration(20))
        view.findViewById<ImageButton>(R.id.imagebutton).setOnClickListener {
            // 갤러리에서 이미지 선택
            resultLauncher.launch("image/*")
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // 선택된 이미지의 URI를 ImageView에 설정
            uri?.let {
                val imageView = view?.findViewById<ImageButton>(R.id.imagebutton)
                imageView?.setImageURI(uri)
                imageLink = uri
                imgaetoserver(imageLink.toString(), storedToken)
            }

        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences 초기화
        sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        mretrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(mypageapi::class.java)
        // MyPageFragment에서 토큰 사용 예시
        storedToken = getStoredToken()
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $storedToken"
        getmypageinfo(storedToken, view)
    }
    private fun getStoredToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }
    private fun getmypageinfo(token: String, view: View) {
        val call: Call<PostData> = mRetrofitAPI.signup(token)
        call.enqueue(object : Callback<PostData> {
            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                if (response.isSuccessful) {
                    // 성공적으로 사용자 정보를 가져왔을 때의 처리
                    val userInfo = response.body()
                    userInfo?.let{
                        Log.d("성공", "username은 ${it.name}")
                        activity?.runOnUiThread{
                            view.findViewById<TextView>(R.id.username)?.text = "${it.name}"
                            view.findViewById<TextView>(R.id.usertemp)?.text = "${it.temp}"
                            view.findViewById<TextView>(R.id.usertype)?.text = "${it.type}"
                            view.findViewById<TextView>(R.id.userintrod)?.text = "  ${it.introd}"
                            val reviews = userInfo.reviews
                            if(reviews !=null){
                                val reviewItemlist = ArrayList<ReviewItem>()
                                for (review in reviews){
                                    val reviewItem = ReviewItem(
                                        title = "  ${review.writer}'s Review",
                                        content = "  ${review.description}"
                                    )
                                    reviewItemlist.add(reviewItem)
                                }
                                updateRecyclerView(reviewItemlist)
                            }
                        }
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
    private fun imgaetoserver(uri: String, token: String){
        val call1: Call<PostData> = mRetrofitAPI.imagetoserver(token, uri)
        call1.enqueue(object : Callback<PostData>{
            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                if(response.isSuccessful){
                    Log.d("이미지 업로드 성공", "나이스")
                }
                else{
                    println("response가 성공적이지 않음")
                }
            }

            override fun onFailure(call: Call<PostData>, t: Throwable) {
                println("뭔가 이상함")
            }

        }
        )
    }
    private fun updateRecyclerView(reviewItemList: List<ReviewItem>) {
        // RecyclerView에 데이터 업데이트
        item_list1.clear()
        item_list1.addAll(reviewItemList)
        reviewAdapter.notifyDataSetChanged()
    }
}