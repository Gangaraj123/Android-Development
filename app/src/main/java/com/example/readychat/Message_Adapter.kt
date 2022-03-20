package com.example.readychat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val message_list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemReceived = 1
    private val itemSent = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            // infalte receive
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.recieved, parent, false)
            ReceivedViewholder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.sent, parent, false)
            SentViewholder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = message_list[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            itemSent
        } else {
            itemReceived
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = message_list[position]
        if (holder.javaClass == SentViewholder::class.java) // for sent view holder
        {
            holder as SentViewholder
            holder.sentmessage.text = currentMessage.message
        } else // for receive view holder
        {
            holder as ReceivedViewholder
            holder.receiverMessage.text = currentMessage.message
        }
    }

    override fun getItemCount(): Int {
        return message_list.size
    }

    class SentViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val sentmessage = itemview.findViewById<TextView>(R.id.txt_sent_msg)!!
    }

    class ReceivedViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val receiverMessage = itemview.findViewById<TextView>(R.id.txt_rec_msg)!!
    }
}
