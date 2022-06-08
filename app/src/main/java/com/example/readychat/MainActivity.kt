package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.readychat.databinding.ActivityMainBinding
import com.example.readychat.ui.Profile.ProfileFragment
import com.example.readychat.ui.Profile.ProfileViewModel
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.User
import com.example.readychat.ui.startups.Login
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yalantis.ucrop.UCrop

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mauth = FirebaseAuth.getInstance()
    private var curr_user: User? = null
    private lateinit var mdbRef: DatabaseReference
    private lateinit var profileFragment: ProfileFragment
    private val profViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        Handler().postDelayed({
            val sscreen = findViewById<ConstraintLayout>(R.id.start_up_screen)
            if (sscreen != null && sscreen.visibility == View.VISIBLE)
                sscreen.visibility = View.GONE
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }, 1500)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val intent = Intent(this@MainActivity, addFreinds::class.java)
            startActivity(intent)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_requests, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        mdbRef = FirebaseDatabase.getInstance().reference
        mdbRef.child("users").child(mauth.uid.toString()).child("user_details")
            .addValueEventListener(object : ValueEventListener {
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
                    } catch (e: Exception) {
                        Log.d("abba", "Some expception occured")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            try {

                profViewModel.setData(UCrop.getOutput(data!!).toString())
            } catch (e: Exception) {
                Log.d("abba", "error occured = " + e.message)
            }


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            val intent: Intent = Intent(this@MainActivity, Login::class.java)
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