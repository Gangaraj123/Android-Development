package com.example.testing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val Gallery = 100
    private val Gallery_Permission_Code = 101
    var img2asc: Image2Ascii? = null  // Image2Ascii from antoher class
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Do_Initial_steps() // setting onclick on buttons
        img2asc = Image2Ascii(this) // passing context to image2ascii
    }

    private fun Do_Initial_steps() {

        val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener {
            PickImage()
        }
        val btn2: Button = findViewById(R.id.btn2)
        btn2.setOnClickListener {
            img2asc?.img2ascii()
        }
    }


    // Function to handle image picker, launching and intent
    fun PickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    // handling received intent
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                // hide textview and show image
                val txt: TextView = findViewById(R.id.txt)
                txt.visibility = View.GONE
                val img: ImageView = findViewById(R.id.img1)
                img.visibility = View.VISIBLE
                img.setImageURI(it.data?.data)
            }
        }
}