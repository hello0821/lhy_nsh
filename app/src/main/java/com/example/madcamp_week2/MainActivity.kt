package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(Posts(), "POSTS")
        adapter.addFragment(Chatting(), "CHATTING")
        adapter.addFragment(Recommendation(), "RECOMMENDATION")
        adapter.addFragment(Mypage(), "MY PAGE")

        viewPager = findViewById(R.id.viewPager)
        val tab: TabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = adapter
        tab.setupWithViewPager(viewPager)

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 1) { // "CHATTING" 탭의 인덱스, 0부터 시작
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택 해제되었을 때의 동작
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택되었을 때의 동작
            }
        })

    }


}