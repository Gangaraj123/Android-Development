package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ActionMenuView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
            val intent = Intent(this, Login::class.java)
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
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white))
        tabs.setupWithViewPager(viewPager)

        setCustomMenuInflater()
    }
    private fun setCustomMenuInflater() {
        val bottombar:ActionMenuView=findViewById(R.id.toolbar_bottom)
        val bottomMenu=bottombar.menu
        menuInflater.inflate(R.menu.menu,bottomMenu)
        for (i in 0 until bottomMenu.size()) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(object :
                MenuItem.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return onOptionsItemSelected(item!!)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mauth.signOut()
            val intent: Intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

}