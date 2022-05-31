package com.example.readychat.ui.main

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.readychat.ui.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class ImgManager {
    fun putImageInStorage(storageReference: StorageReference,uri:Uri,sender:String,receiver:String,databaseReference: DatabaseReference)
    {
        val MsgObj=Message(null,FirebaseAuth.getInstance().uid.toString(),null)
        var result: String? =null
        storageReference.child("Images").child(uri.lastPathSegment!!).putFile(uri)
            .addOnSuccessListener() {
                it.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        MsgObj.ImageUrl=it.toString()
                        databaseReference.child("chats").child(sender)
                            .child("messages").push().setValue(MsgObj)
                            .addOnSuccessListener {
                                databaseReference.child("chats").child(receiver)
                                    .child("messages").push().setValue(MsgObj)
                            }
                     }
                    .addOnFailureListener()
                    {
                        Log.d("abba","failed - "+it.message)
                    }
            }
            .addOnFailureListener()
            {

                        Log.d("abba","failed - "+it.message)
            }
        Log.d("abba","result = "+result.toString())
    }
     fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e ->
                    Log.d(
                        "abba",
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }
}