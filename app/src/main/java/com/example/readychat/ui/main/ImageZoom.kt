package com.example.readychat.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.readychat.R

class ImageZoom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)
        val img_url=intent.getStringExtra("imgurl")
        val img_view=findViewById<ImageView>(R.id.image_view)
        if (img_url != null) {
            ImgManager.loadImageIntoView(img_view,img_url)
        }

        findViewById<ImageButton>(R.id.back_btn).setOnClickListener {
            finish()
        }
    }
}