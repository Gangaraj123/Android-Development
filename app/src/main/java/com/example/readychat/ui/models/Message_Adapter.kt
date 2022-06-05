package com.example.readychat.ui.models

import android.animation.Animator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.R
import com.example.readychat.ui.main.ImageZoom
import com.example.readychat.ui.main.ImgManager
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val message_list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemReceivedImg = 1
    private val itemReceivedMsg = 2
    private lateinit var temp_timeStamp: Date
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yy  hh:mm aa")
    private val itemSentImg = 3
    private var current_aimator: Animator? = null
    private var shortAnimationDuration: Int = 0
    private val itemSentmsg = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            // infalte receive
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.received_image, parent, false)
            ReceivedImgViewholder(view)
        } else if (viewType == 2) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.recieved, parent, false)
            ReceivedViewholder(view)
        } else if (viewType == 3) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.sent_image, parent, false)
            SentImgViewholder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.sent, parent, false)
            SentViewholder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = message_list[position]
        return if (FirebaseAuth.getInstance().uid.equals(currentMessage.senderId)) {
            if (currentMessage.message == null)
                itemSentImg
            else itemSentmsg
        } else {
            if (currentMessage.message == null)
                itemReceivedImg
            else itemReceivedMsg
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = message_list[position]
        temp_timeStamp = Date(currentMessage.timestamp)
        if (holder.javaClass == SentViewholder::class.java) // for sent view holder
        {
            holder as SentViewholder
            holder.sentmessage.text = currentMessage.message
            try {
                holder.timeview.text = simpleDateFormat.format(temp_timeStamp)
            } catch (e: Exception) {
            }

        } else if (holder.javaClass == SentImgViewholder::class.java) {
            holder as SentImgViewholder
            try {
                holder.timeview.text = simpleDateFormat.format(temp_timeStamp)
            } catch (e: Exception) {
            }
            if (currentMessage.ImageUrl == null)
                holder.sentimg.setImageResource(R.drawable.imgload)
            else
                ImgManager.loadImageIntoView(holder.sentimg, currentMessage.ImageUrl,false)
            holder.sentimg.setOnClickListener {
                val intent = Intent(it.context, ImageZoom::class.java)
                intent.putExtra("imgurl", currentMessage.ImageUrl)
                it.context.startActivity(intent)
            }
        } else if (holder.javaClass == ReceivedViewholder::class.java) {
            holder as ReceivedViewholder
            try {
                holder.timeview.text = simpleDateFormat.format(temp_timeStamp)
            } catch (e: Exception) {
            }
            holder.receiverMessage.text = currentMessage.message
        } else {
            holder as ReceivedImgViewholder
            try {

                holder.timeview.text = simpleDateFormat.format(temp_timeStamp)
            } catch (e: Exception) {
            }
            if (currentMessage.ImageUrl == null)
                holder.receiverimg.setImageResource(R.drawable.imgload)
            else
                ImgManager.loadImageIntoView(holder.receiverimg, currentMessage.ImageUrl,false)
            holder.receiverimg.setOnClickListener {
                val intent = Intent(it.context, ImageZoom::class.java)
                intent.putExtra("imgurl", currentMessage.ImageUrl)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return message_list.size
    }

    class SentViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val sentmessage = itemview.findViewById<TextView>(R.id.txt_sent_msg)!!
        val timeview = itemview.findViewById<TextView>(R.id.time_view)
    }

    class SentImgViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview = itemview.findViewById<TextView>(R.id.time_view)
        val sentimg = itemview.findViewById<ImageView>(R.id.SmessageImageView)!!
    }

    class ReceivedViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview = itemview.findViewById<TextView>(R.id.time_view)
        val receiverMessage = itemview.findViewById<TextView>(R.id.txt_rec_msg)!!
    }

    class ReceivedImgViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview = itemview.findViewById<TextView>(R.id.time_view)
        val receiverimg = itemview.findViewById<ImageView>(R.id.RmessageImageView)!!
    }
}
