package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
 import com.example.readychat.databinding.ActivityMainBinding
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.User
import com.example.readychat.ui.startups.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mauth=FirebaseAuth.getInstance()
    private  var curr_user: User?=null
    private lateinit var mdbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(mauth.uid==null)
        {
            val intent: Intent=Intent(this@MainActivity,Login::class.java)
            finish()
            startActivity(intent)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        mdbRef=FirebaseDatabase.getInstance().reference
            mdbRef.child("users").child(mauth.uid.toString())
                .addValueEventListener(object :ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        curr_user = snapshot.getValue(User::class.java)
                        try {
                            findViewById<TextView>(R.id.curr_user_name).text = curr_user?.name


                        findViewById<TextView>(R.id.curr_user_description).text =
                            curr_user?.about.toString()
                        if (curr_user?.profile_pic_url != null)
                            ImgManager.loadImageIntoView(
                                findViewById(R.id.profile),
                                curr_user?.profile_pic_url.toString()
                            )
                    }
                        catch (e:Exception)
                        {
                            Log.d("abba","Some expception occured")
                        }
                }


                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut()
            val intent: Intent=Intent(this@MainActivity,Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}