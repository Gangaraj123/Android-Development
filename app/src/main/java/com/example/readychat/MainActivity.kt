package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var user_list: ArrayList<user>
    private lateinit var adapter: user_adapter
    private lateinit var mauth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference
    private lateinit var load_gif: pl.droidsonroids.gif.GifImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        set_top_bars()
        load_gif = findViewById(R.id.loading)
        mauth = FirebaseAuth.getInstance()
        if (mauth.currentUser == null) { // if no user is logged in
            val intent: Intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // finsih main activity
            return
        }
        mdbRef = FirebaseDatabase.getInstance().reference
        user_list = ArrayList()
        adapter = user_adapter(this, user_list)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mdbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_list.clear()
                for (postSnapshot in snapshot.children) {
                    val current_user = postSnapshot.getValue(user::class.java)
                    if (mauth.currentUser?.uid != current_user?.uid)
                        user_list.add(current_user!!)
                }
                adapter.notifyDataSetChanged()
                load_gif.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        userRecyclerView.visibility = View.VISIBLE
    }

    fun set_top_bars() {

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.title=null // remove default title
        var actionbarview: View = LayoutInflater.from(this).inflate(R.layout.action_bar_home, null)
        supportActionBar?.customView = actionbarview
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.action_bar_bg))
        supportActionBar?.show()

        val window = this.window
        window.statusBarColor = this.resources.getColor(R.color.blue)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
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