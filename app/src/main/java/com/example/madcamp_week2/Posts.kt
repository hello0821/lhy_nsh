package com.example.madcamp_week2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Posts : Fragment() {
    private lateinit var postAdapter: PostAdapter
    private lateinit var rv_list: RecyclerView
    private val item_list = ArrayList<PostItem>()
    private val fullItemList = ArrayList<PostItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.posts, container, false)
        rv_list = view.findViewById(R.id.rv_list)
        val add_write_btn = view.findViewById<Button>(R.id.writePostBtn)
        val searchView = view.findViewById<SearchView>(R.id.search)

        //GET /posts
        fullItemList.add(PostItem(0,"프랑스 여행 동행 구함", "신짱구" , "2024-02-01 ~ 2024-02-05","파리, 니스", "같이 여행가요"))
        fullItemList.add(PostItem(1,"이탈리아 여행 동행 구함", "신짱아","2024-02-01 ~ 2024-02-05", "로마, 피렌체", "여러분 여행가요"))

        item_list.addAll(fullItemList)

        postAdapter = PostAdapter(item_list)
        rv_list.adapter = postAdapter
        rv_list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

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

        postAdapter.itemClickListener = object: PostAdapter.OnItemClickListener {
            override fun onItemclick(position: Int) {
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
    private fun searchPosts(query: String) {
        val filteredList = ArrayList(fullItemList.filter {
            it.title.contains(query, ignoreCase = true) // 대소문자 구분 없이
            it.loc.contains(query, ignoreCase=true)
        })
        postAdapter.updateList(filteredList)
    }
}