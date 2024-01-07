package com.example.madcamp_week2

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
    }
}