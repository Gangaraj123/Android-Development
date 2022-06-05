package com.example.readychat.ui.models

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.R
import com.google.firebase.database.FirebaseDatabase

private val mdbref = FirebaseDatabase.getInstance().reference

class Request_Adapter(val conext: Context?, val request_list: ArrayList<Friend_Request>) :
    RecyclerView.Adapter<Request_Adapter.Request_View_Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Request_View_Holder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.request_layout, parent, false)
        return Request_View_Holder(view)
    }

    override fun onBindViewHolder(holder: Request_View_Holder, position: Int) {
        val curr_request = request_list[position]
        holder.rnam.text = curr_request.senderName
        holder.rmail.text = curr_request.senderMail
        holder.accept_btn.isEnabled = true
        holder.decline_btn.isEnabled = true
        holder.decline_btn.setOnClickListener(View.OnClickListener {
            mdbref.child("users").child(curr_request.receiverId)
                .child("friend_requests").child(curr_request.senderId).removeValue()
        })
        holder.accept_btn.setOnClickListener(View.OnClickListener {
            Log.d("abba", "Reached here bro")
            mdbref.child("users").child(curr_request.senderId)
                .child("friends_list").child(curr_request.receiverId)
                .setValue(System.currentTimeMillis())
                .addOnSuccessListener {
                    mdbref.child("users").child(curr_request.receiverId)
                        .child("friends_list").child(curr_request.senderId)
                        .setValue(System.currentTimeMillis())
                        .addOnSuccessListener {
                            holder.accept_btn.isEnabled = false
                            holder.decline_btn.isEnabled = false
                            mdbref.child("users").child(curr_request.receiverId)
                                .child("friend_requests").child(curr_request.senderId).removeValue()
                            mdbref.child("users").child(curr_request.receiverId)
                                .child("friends_list").child(curr_request.senderId).child("time")
                                .setValue(System.currentTimeMillis())
                            mdbref.child("users").child(curr_request.senderId)
                                .child("friends_list").child(curr_request.receiverId).child("time")
                                .setValue(System.currentTimeMillis())

                        }
                        .addOnFailureListener {
                            Log.d("abba", "Failed in step 2")
                        }
                }
                .addOnFailureListener {
                    Log.d("abba", "Failed in step 1")
                }
        })
        holder.item.setOnClickListener(View.OnClickListener {
            if (holder.hiddenView.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.cardview, AutoTransition())
                holder.hiddenView.visibility = View.GONE

            } else {
                TransitionManager.beginDelayedTransition(holder.cardview, AutoTransition())
                holder.hiddenView.visibility = View.VISIBLE

            }
        })
    }


    override fun getItemCount(): Int {
        return request_list.size
    }

    class Request_View_Holder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val cardview = itemview.findViewById<CardView>(R.id.request_card_view)
        val rnam = itemview.findViewById<TextView>(R.id.req_user_name)
        val rmail = itemview.findViewById<TextView>(R.id.req_uer_email)
        val hiddenView = itemview.findViewById<ConstraintLayout>(R.id.hidden_view)
        val item = itemview.findViewById<LinearLayout>(R.id.reqest_item)
        val accept_btn = itemview.findViewById<Button>(R.id.accept_btn)
        val decline_btn = itemview.findViewById<Button>(R.id.decline_btn)
    }
}