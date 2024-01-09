package com.example.madcamp_week2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
    var teamState: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra("currentState")) {
            teamState = intent.getStringExtra("currentState")
        }

        setupViewPagerAndTabs()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("currentState")) {
            teamState = intent.getStringExtra("currentState")
        }
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val tabName = intent.getStringExtra("returnToTab")

        //tab 이동
        if (tabName != null) {
            val tabIndex = when (tabName) {
                "Posts" -> 0
                "Chatting" -> 1
                "Recommendation" -> 2
                "My Page" -> 3
                else -> 0
            }
            println("In handleIntent")
            viewPager.currentItem = tabIndex
            moveToTab(tabIndex)
        }
    }

    fun loadConfirmFragment() {
        val confirmFragment = ConfirmFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_container, confirmFragment)
            .addToBackStack(null)
            .commit()
    }

    fun loadReviewFragment() {
        val reviewFragment = ReviewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_container, reviewFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupViewPagerAndTabs() {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(Posts(), "POSTS")
        adapter.addFragment(Chatting(), "CHATTING")
        adapter.addFragment(Recommendation(), "RECOMMENDATION")
        adapter.addFragment(Mypage(), "MY PAGE")

        viewPager = findViewById(R.id.viewPager)
        val tab: TabLayout = findViewById(R.id.tabLayout)
        val frameView = findViewById<FrameLayout>(R.id.detail_container)

        viewPager.adapter = adapter
        tab.setupWithViewPager(viewPager)

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 1) { // "CHATTING" 탭의 인덱스, 0부터 시작
                    frameView.visibility = View.VISIBLE
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    startActivity(intent)
                } else {
                    frameView.visibility = View.INVISIBLE
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

    fun moveToPostsFragment() {
        viewPager.currentItem = 0 // "Posts" 탭의 인덱스로 설정
        val currentFragment = supportFragmentManager.findFragmentById(R.id.totalResultView)
        if (currentFragment is ResultRecommendation) {
            val postFragment = Posts()
            supportFragmentManager.beginTransaction()
                .replace(R.id.totalResultView, postFragment)
                .commit()
        }
    }
    fun moveToTab(tabIndex: Int) {
        viewPager.setCurrentItem(tabIndex, true)
    }


}