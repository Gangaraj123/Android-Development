package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StartScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)
        val intent = Intent(this, MainActivity::class.java)
        window.statusBarColor=ContextCompat.getColor(this,R.color.white)
        Handler().postDelayed({
            startActivity(intent)
            finish()

        }, 3000)
    }
}