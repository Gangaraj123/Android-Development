package com.example.readychat

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

// a contract which launches file picker
class OpenFileContract :ActivityResultContracts.OpenDocument(){
    override fun createIntent(context: Context, input: Array<out String>): Intent {
        val intent=super.createIntent(context, input)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        return intent
    }
}