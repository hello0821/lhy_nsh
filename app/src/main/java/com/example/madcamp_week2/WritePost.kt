package com.example.madcamp_week2

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class WritePost : Fragment() {
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var imageLink: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val title = view.findViewById<EditText>(R.id.writeTitle)
        val loc = view.findViewById<EditText>(R.id.writeLoc)
        val content = view.findViewById<EditText>(R.id.writeContent)

        writeBtn.setOnClickListener {
            //POST /post 로 백과 소통
            //버튼 클릭 시, title, loc, content, date, image, writer 에 대한 정보를 서버에 저장

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

}