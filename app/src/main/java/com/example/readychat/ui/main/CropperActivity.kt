package com.example.readychat.ui.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.readychat.R
import com.example.readychat.ui.Profile.result_Uri
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class CropperActivity : AppCompatActivity() {
     private var result=""
     private lateinit var fileuri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropper)
        readIntent()
        val destUri=StringBuilder(UUID.randomUUID().toString()).append(".jpg")
            .toString()
        val options=UCrop.Options()
         UCrop.of(fileuri,Uri.fromFile(File(cacheDir,destUri)))
            .withOptions(options)
            .withAspectRatio(0F,0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000,2000)
            .start(this@CropperActivity)

    }
    private fun readIntent()
    {
        val intent=getIntent()
        if(intent.extras!=null)
        {
            result=intent.getStringExtra("ImageUri") !!
        fileuri= Uri.parse(result)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==UCrop.REQUEST_CROP)
        {
            val resultUri= data?.let { UCrop.getOutput(it) }
            result_Uri=resultUri
            finish()
        }
    }
}