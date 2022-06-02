package com.example.readychat.ui.main

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.readychat.ui.models.Message
import com.example.readychat.ui.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

internal object ImgManager {
    fun putImageInStorage(
        storageReference: StorageReference,
        uri: Uri,
        sender: String,
        receiver: String,
        databaseReference: DatabaseReference
    ) {
        val MsgObj = Message(null, FirebaseAuth.getInstance().uid.toString(), null)
        var result: String? = null
        storageReference.child("Images").child(uri.lastPathSegment!!).putFile(uri)
            .addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        MsgObj.ImageUrl = it.toString()
                        databaseReference.child("chats").child(sender)
                            .child("messages").push().setValue(MsgObj)
                            .addOnSuccessListener {
                                databaseReference.child("chats").child(receiver)
                                    .child("messages").push().setValue(MsgObj)
                            }
                    }
                    .addOnFailureListener()
                    {
                        Log.d("abba", "failed - " + it.message)
                    }
            }
            .addOnFailureListener()
            {

                Log.d("abba", "failed - " + it.message)
            }
    }

    fun putProfile_In_Storage(
        storageReference: StorageReference,
        uri: Uri?,
        user: User,
        databaseReference: DatabaseReference
    ) {
        if (uri != null) {
            storageReference.child("Profiles").child(uri.lastPathSegment!!)
                .putFile(uri)
                .addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        user.profile_pic_url = it.toString()
                        databaseReference.child("users").child(user.uid!!)
                            .setValue(user)
                    }
                }
        } else databaseReference.child("users").child(user.uid!!)
            .setValue(user)
    }

    fun LoadProfileIntoView(
        view: ImageView,
        url: String?,
        button: Button,
        prog: ProgressBar,
        pname: TextView,
        pabout: TextView
    ) {
        if (url != null && url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                    prog.visibility = View.GONE
                    button.text = "Edit"
                    button.visibility = View.VISIBLE
                    pname.visibility = View.VISIBLE
                    pabout.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    Log.d(
                        "abba",
                        "Getting download url was not successful.",
                        e
                    )
                    prog.visibility = View.GONE
                    button.text = "Edit"
                    button.visibility = View.VISIBLE
                    pname.visibility = View.VISIBLE
                    pabout.visibility = View.VISIBLE
                }
        } else {
            Glide.with(view.context).load(url).into(view)
            prog.visibility = View.GONE
            button.text = "Edit"
            button.visibility = View.VISIBLE
            pname.visibility = View.VISIBLE
            pabout.visibility = View.VISIBLE
        }

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