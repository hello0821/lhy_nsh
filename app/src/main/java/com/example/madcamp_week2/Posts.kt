package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface postapi{
    @GET("/getallposting")
    suspend fun getpost(): Response<List<PostItem>>
}
class Posts : Fragment() {
    private lateinit var postAdapter: PostAdapter
    private lateinit var rv_list: RecyclerView
    private val item_list = ArrayList<PostItem>()
    private val fullItemList = ArrayList<PostItem>()
    private lateinit var mretrofit: Retrofit
    private lateinit var mRetrofitAPI: postapi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mretrofit = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mRetrofitAPI = mretrofit.create(postapi::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.posts, container, false)
        rv_list = view.findViewById(R.id.rv_list)
        val add_write_btn = view.findViewById<Button>(R.id.writePostBtn)
        val searchView = view.findViewById<SearchView>(R.id.search)
        postAdapter = PostAdapter(item_list)
        //GET /posts
        lifecycleScope.launch {
            val getUserInfoDeferred = async { getposting() }
            getUserInfoDeferred.await() // getUserInfo 함수가 완료될 때까지 기다립니다.
            updaterecyclerview()
        }

        // SearchView 리스너 설정
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchPosts(it)
                }
                return true
            }
        })
/*
        postAdapter.itemClickListener = object: PostAdapter.OnItemClickListener {
            override fun onItemclick(position: Int) {
                println("일단 클릭은 됨")
                val item = item_list[position]
                val detailPostFragment = DetailPost()

                val args = Bundle()
                args.putParcelable("detailPostItem", item)
                detailPostFragment.arguments = args

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.postboard, detailPostFragment)
                    .addToBackStack(null)
                    .commit()

                postAdapter.notifyDataSetChanged()

            }
        }
*/
        add_write_btn.setOnClickListener{
            val writePostFragment = WritePost()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.postboard, writePostFragment)
                .addToBackStack(null)
                .commit()

        }

        return view
    }
    private suspend fun getposting(){
        try {
            val response = mRetrofitAPI.getpost()
            if(response.isSuccessful){
                val data = response.body()

                data?.forEach { i ->
                    fullItemList.add(PostItem(i._id, i.content, i.date1, i.date2, i.imgfilename,
                        i.location, i.title, i.writerid))
                }
                println("설마설마설마" + fullItemList)
            }
        }
        catch(e: Exception){
            Log.e("failgetpost", "fail${e.localizedMessage}")
        }
    }
    private fun updaterecyclerview(){
        item_list.addAll(fullItemList)
        println("여기를 보세요오" + item_list)
        postAdapter = PostAdapter(item_list)
        rv_list.adapter = postAdapter
        rv_list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        postAdapter.itemClickListener = object: PostAdapter.OnItemClickListener {
            override fun onItemclick(position: Int) {
                println("일단 클릭은 됨")
                val item = item_list[position]
                val detailPostFragment = DetailPost()

                val args = Bundle()
                args.putParcelable("detailPostItem", item)
                detailPostFragment.arguments = args

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.postboard, detailPostFragment)
                    .addToBackStack(null)
                    .commit()

                postAdapter.notifyDataSetChanged()

            }
        }
    }
    private fun searchPosts(query: String) {
        val filteredList = ArrayList(fullItemList.filter {
            it.title.contains(query, ignoreCase = true) // 대소문자 구분 없이
            it.location.contains(query, ignoreCase=true)
        })
        postAdapter.updateList(filteredList)
    }
}