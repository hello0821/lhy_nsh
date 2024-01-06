package com.example.madcamp_week2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 페이지 데이터 로드
        val list = listOf(login(), Posts(), Chatting(), Recommendation(), Mypage())

        // 아답터 생성 및 연결
        val pagerAdapter = FragmentPagerAdapter(list, this)
        binding.viewPager.adapter = pagerAdapter

        // 탭 메뉴 제목 설정
        val titles = listOf("login", "Posts", "Chatting", "Recommendation", "Mypage")

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        // 현재 활성화된 프래그먼트 추적
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentFragment = list[position]
            }
        })
    }
}
class FragmentPagerAdapter(
    private val fragmentList: List<Fragment>,
    fragmentActivity: AppCompatActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = fragmentList.size
    override fun createFragment(position: Int) = fragmentList[position]
}