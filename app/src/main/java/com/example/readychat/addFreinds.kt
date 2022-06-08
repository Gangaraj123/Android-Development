package com.example.readychat

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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
    private lateinit var user_not_found: LinearLayout
    private lateinit var alreadyFriend: MaterialCardView
    private lateinit var alreadyfriendname: TextView
    private lateinit var alreadyfriendemail: TextView
    private lateinit var alreadyfriendabout: TextView
    private lateinit var search_error_mgs: TextView
    private lateinit var mdbRef: DatabaseReference
    private var result: User? = null
    private lateinit var background: View
    private lateinit var curr_user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.drawable.nothing, R.drawable.nothing)
        setContentView(R.layout.activity_add_freinds)
        background = findViewById(R.id.my_addfriend_layout)
        if (savedInstanceState == null) {
            background.visibility = View.INVISIBLE
            val viewTreeObserver = background.viewTreeObserver

            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity()
                        background.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }

            found_user_about = findViewById(R.id.foudn_user_about)
        found_user_details = findViewById(R.id.searched_user_details)
        found_user_email = findViewById(R.id.found_user_email)
        found_user_name = findViewById(R.id.found_user_name)
        user_not_found = findViewById(R.id.no_user_found)
        alreadyFriend = findViewById(R.id.already_friend_user_details)
        findViewById<ImageButton>(R.id.back_btn).setOnClickListener {
            onBackPressed()
        }
        alreadyfriendname = findViewById(R.id.friend_user_name)
        alreadyfriendabout = findViewById(R.id.friend_user_about)
        search_error_mgs = findViewById(R.id.error_msg)
        alreadyfriendemail = findViewById(R.id.friend_user_email)
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
                        result = null
                        for (x in snapshot.children) {
                            if (x.child("user_details").child("email")
                                    .getValue(String::class.java)!! == email_to_search
                            ) {
                                result = x.child("user_details").getValue(User::class.java)!!
                                break
                            }
                        }
                        if (result != null) {
                            if (result?.uid == curr_user.uid) {
                                alreadyfriendname.text = curr_user.name!!
                                alreadyfriendabout.text = curr_user.about!!
                                alreadyfriendemail.text = curr_user.email!!
                                search_error_mgs.text = "Its you!!"
                                search_error_mgs.setTextColor(Color.parseColor("#FF0000"))
                                if (alreadyFriend.visibility != View.VISIBLE)
                                    alreadyFriend.visibility = View.VISIBLE
                                if (found_user_details.visibility == View.VISIBLE)
                                    found_user_details.visibility = View.GONE
                                search_btn.complete(true)
                            } else {
                                mdbRef.child("users").child(curr_user.uid!!)
                                    .child("friends_list").child(result?.uid!!)
                                    .get().addOnSuccessListener {
                                        if (it.exists()) {
                                            alreadyfriendname.text = result?.name
                                            alreadyfriendabout.text = result?.about
                                            alreadyfriendemail.text = result?.email
                                            search_error_mgs.text = "Already your Friend!!"
                                            search_error_mgs.setTextColor(Color.parseColor("#00FF00"))
                                            if (alreadyFriend.visibility != View.VISIBLE)
                                                alreadyFriend.visibility = View.VISIBLE
                                            if (found_user_details.visibility == View.VISIBLE)
                                                found_user_details.visibility = View.GONE
                                        } else {

                                            found_user_name.text = result!!.name
                                            found_user_email.text = result!!.email
                                            found_user_about.text = result!!.about
                                            if (alreadyFriend.visibility == View.VISIBLE)
                                                alreadyFriend.visibility = View.GONE
                                            if (found_user_details.visibility != View.VISIBLE)
                                                found_user_details.visibility = View.VISIBLE
                                            req_send_button.cancel()
                                        }
                                        search_btn.complete(true)
                                        if (user_not_found.visibility == View.VISIBLE)
                                            user_not_found.visibility = View.GONE
                                        searchbox.setText("")
                                        req_send_button.isEnabled = true

                                    }
                                    .addOnFailureListener {
                                        search_btn.complete(false)
                                        if (user_not_found.visibility != View.VISIBLE)
                                            user_not_found.visibility = View.VISIBLE
                                        if (alreadyFriend.visibility == View.VISIBLE)
                                            alreadyFriend.visibility = View.GONE
                                        if (found_user_details.visibility == View.VISIBLE)
                                            found_user_details.visibility = View.GONE
                                    }

                            }
                        } else {
                            search_btn.complete(false)
                            if (user_not_found.visibility != View.VISIBLE)
                                user_not_found.visibility = View.VISIBLE
                            if (alreadyFriend.visibility == View.VISIBLE)
                                alreadyFriend.visibility = View.GONE
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
                result?.name,
                curr_user.uid,
                result?.uid,
                "",
                curr_user.email,
                result?.email
            )
            mdbRef.child("users")
                .child(result?.uid!!).child("friend_requests")
                .child(curr_user.uid!!).setValue(newrequest).addOnSuccessListener {
                    req_send_button.complete(true)
                    req_send_button.isEnabled = false
                }
                .addOnFailureListener {
                    req_send_button.complete(true)
                    req_send_button.isEnabled = false
                }
        })
    }

    private fun circularRevealActivity() {
        val cx = background.right - getDips(44)
        val cy = background.bottom - getDips(44)
        val finalRadius = background.width.coerceAtLeast(background.height).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            background,
            cx,
            cy, 0f,
            finalRadius
        )
        circularReveal.duration = 300
        background.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun getDips(dps: Int): Int {
        val resources: Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onBackPressed() {
        val cx: Int = background.width - getDips(44)
        val cy: Int = background.bottom - getDips(44)
        val finalRadius: Int = background.width.coerceAtLeast(background.height)
        val circularReveal: Animator =
            ViewAnimationUtils.createCircularReveal(background, cx, cy, finalRadius.toFloat(), 0f)
        circularReveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator?) {}
            override fun onAnimationEnd(animator: Animator?) {
                background.visibility = View.INVISIBLE
                finish()
            }

            override fun onAnimationCancel(animator: Animator?) {}
            override fun onAnimationRepeat(animator: Animator?) {}
        })
        circularReveal.duration = 300
        circularReveal.start()
    }


}