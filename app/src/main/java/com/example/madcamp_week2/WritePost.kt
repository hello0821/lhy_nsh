package com.example.madcamp_week2

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

data class Posting(
    val _id: String,
    val title: String,
    val imgfilename: String,
    val location: String,
    val content: String,
    val date1: String,
    val date2: String,
    val writerid: String
)
interface postingapi {
    @Multipart
    @POST("/postingtoserver")
    fun postingtoserver(@Part("data") data: Posting,
                        @Part image: MultipartBody.Part ): Call<id>
    @GET("/mypage")
    suspend fun signup(@Header("Authorization") token:String): Response<PostData>
}
class WritePost : Fragment() {
    var mretrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var mRetrofitAPI = mretrofit.create(postingapi::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var imageLink: Uri
    private lateinit var storedToken: String
    private var writerid: String = "이게 보이면 안됩니다 진짜"


    override fun onCreate(savedInstanceState: Bundle?) {
//        imageLink = Uri.parse("")
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        storedToken = getStoredToken()
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $storedToken"
        lifecycleScope.launch {
            val getUserInfoDeferred = async { getid(storedToken) }
            getUserInfoDeferred.await() // getUserInfo 함수가 완료될 때까지 기다립니다.
        }
        // ActivityResultLauncher 초기화
        resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // 선택된 이미지의 URI를 ImageView에 설정
            uri?.let {
                val imageView = view?.findViewById<ImageView>(R.id.viewSelectedPic)
                imageView?.setImageURI(uri)
                imageLink = uri
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.write_post, container, false)
        val writeBtn = view.findViewById<Button>(R.id.addPostBtn)
        val addPicBtn = view.findViewById<Button>(R.id.addPicBtn)
        val title = view.findViewById<EditText>(R.id.writeTitle).text
        val loc = view.findViewById<EditText>(R.id.writeLoc).text
        val content = view.findViewById<EditText>(R.id.writeContent).text
        val date1 = view.findViewById<DatePicker>(R.id.datePicker1)
        val date2 = view.findViewById<DatePicker>(R.id.datePicker2)
        val postimg = view.findViewById<ImageView>(R.id.viewSelectedPic)
        val year1 = date1.year
        val month1=date1.month
        val day1=date1.dayOfMonth
        val year2=date2.year
        val month2=date2.month
        val day2=date2.dayOfMonth
        val calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.YEAR, year1)
        calendar1.set(Calendar.MONTH, month1)
        calendar1.set(Calendar.DAY_OF_MONTH, day1)
        val calendar2 = Calendar.getInstance()
        calendar2.set(Calendar.YEAR, year2)
        calendar2.set(Calendar.MONTH, month2)
        calendar2.set(Calendar.DAY_OF_MONTH, day2)

        // SimpleDateFormat을 사용하여 날짜를 원하는 형식으로 포맷
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate1 = dateFormat1.format(calendar1.time)
        val dateFormat2 = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate2 = dateFormat2.format(calendar2.time)

        writeBtn.setOnClickListener {
            println("여기를 주목하세요~!~!~! ${imageLink}")
            val imagefile = prepareImageFilePart(requireContext(), "image", imageLink)

            //POST /post 로 백과 소통
            //버튼 클릭 시, title, loc, content, date, image, writer 에 대한 정보를 서버에 저장
            println("user id is ${writerid}")
            val posting = Posting("1",title.toString() ,"",
                loc.toString(), content.toString(), formattedDate1,formattedDate2 , writerid)
            println(posting)
            sendtoserver1(posting, imagefile)

            // api 연결 성공 후 다시 postlist로 돌아감
            val postfragment = Posts()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.totalWriteView, postfragment)
                .addToBackStack(null)
                .commit()
        }

        addPicBtn.setOnClickListener {
            // 갤러리에서 이미지 선택
            resultLauncher.launch("image/*")
        }

        return view
    }
    private fun getStoredToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }
    private fun sendtoserver1(data: Posting, imagefile: MultipartBody.Part){
        val posting: Call<id> = mRetrofitAPI.postingtoserver(data, imagefile)
        posting.enqueue(object: Callback<id> {
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
                Log.e("WritePost", "onFailure", t)
            }
        })
    }
    private suspend fun getid(token: String){
        try{
            val response = mRetrofitAPI.signup(token)
            if (response.isSuccessful){
                val userinfo = response.body()
                userinfo?.let{
                    writerid = it._id
                }
            }
        }
        catch(e: Exception){
            Log.e("dfdf", "dfdfdf${e.localizedMessage}")
        }
    }
    private fun prepareImageFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
        println(fileUri)
        val file = File(getRealPathFromUri(context, fileUri)!!)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if(cursor!=null){
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val filePath = cursor?.getString(columnIndex ?: 0)
            cursor?.close()
            return filePath
        }
        return "이거 보이면 안됩니다22"
    }

}