package com.example.readychat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.readychat.databinding.ActivityMainBinding
import com.example.readychat.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mauth = FirebaseAuth.getInstance()
        if (mauth.currentUser == null) { // if no user is logged in
            val intent: Intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // finsih main activity
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setSelectedTabIndicatorColor(resources.getColor(R.color.white))
        tabs.setupWithViewPager(viewPager)

    }
}