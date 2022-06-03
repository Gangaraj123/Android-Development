package com.example.readychat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readychat.ui.models.Friend_Request
import com.example.readychat.ui.models.User
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class addFreinds : AppCompatActivity() {
    private lateinit var found_user_details: MaterialCardView
    private lateinit var found_user_name: TextView
    private lateinit var found_user_email: TextView
    private lateinit var found_user_about: TextView
    private lateinit var req_send_button: com.flod.loadingbutton.LoadingButton
    private lateinit var search_btn: com.flod.loadingbutton.LoadingButton
    private lateinit var searchbox: EditText
    private lateinit var mdbRef: DatabaseReference
    private lateinit var result: User
    private lateinit var curr_user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_freinds)
        found_user_about = findViewById(R.id.foudn_user_about)
        found_user_details = findViewById(R.id.searched_user_details)
        found_user_email = findViewById(R.id.found_user_email)
        found_user_name = findViewById(R.id.found_user_name)
        req_send_button = findViewById(R.id.req_send_btn)
        mdbRef = FirebaseDatabase.getInstance().reference
        searchbox = findViewById(R.id.search_box)
        search_btn = findViewById(R.id.search_btn)
        mdbRef.child("users").child(FirebaseAuth.getInstance().uid!!)
            .child("user_details").get().addOnSuccessListener {
                curr_user = it.getValue(User::class.java)!!
            }
        search_btn.setOnClickListener(View.OnClickListener {
            val email_to_search = searchbox.text.toString()
            search_btn.start()
            mdbRef.child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (x in snapshot.children) {
                            if (x.child("user_details").child("email")
                                    .getValue(String::class.java)!! == email_to_search
                            ) {
                                result = x.child("user_details").getValue(User::class.java)!!
                                break
                            }
                        }
                        if (result != null) {
                            search_btn.complete(true)
                            found_user_name.text = result.name
                            found_user_email.text = result.email
                            found_user_about.text = result.about
                            if (req_send_button.text != "Add request") {
                                req_send_button.text = "Add request"
                            }
                            if (found_user_details.visibility != View.VISIBLE)
                                found_user_details.visibility = View.VISIBLE
                            searchbox.setText("")
                            if (req_send_button.text == "Request sent") {
                                req_send_button.text = "Add request"
                                req_send_button.isEnabled = true
                            }
                        } else {
                            if (found_user_details.visibility == View.VISIBLE)
                                found_user_details.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

        })
        req_send_button.setOnClickListener(View.OnClickListener {
            req_send_button.start()
            var newrequest = Friend_Request(
                curr_user.name,
                result.name,
                curr_user.uid,
                result.uid,
                "",
                curr_user.email,
                result.email
            )
            mdbRef.child("users")
                .child(result.uid!!).child("friend_requests")
                .child(curr_user.uid!!).setValue(newrequest).addOnSuccessListener {
                    req_send_button.complete(true)
                    req_send_button.text = "Request sent"
                    req_send_button.isEnabled = false
                }
        })
    }
}