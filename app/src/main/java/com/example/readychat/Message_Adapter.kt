package com.example.readychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class Message_Adapter(val message_list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val Item_Received = 1
    val Item_sent = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // infalte receive
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recieved, parent, false)
            return ReceivedViewholder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sent, parent, false)
            return sentViewholder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val curr_msg = message_list[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(curr_msg.senderId)) {
            return Item_sent
        } else {
            return Item_Received
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curr_msg = message_list[position]
        if (holder.javaClass == sentViewholder::class.java) // for sent view holder
        {
            val viewholder = holder as sentViewholder
            holder.sentmessage.text = curr_msg.message
        } else // for receive view holder
        {
            val viewholder = holder as ReceivedViewholder
            holder.Received_message.text = curr_msg.message
        }
    }

    override fun getItemCount(): Int {
        return message_list.size
    }

    class sentViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val sentmessage = itemview.findViewById<TextView>(R.id.txt_sent_msg)
    }

    class ReceivedViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val Received_message = itemview.findViewById<TextView>(R.id.txt_rec_msg)
    }
}
