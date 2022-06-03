package com.example.readychat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.Message
import com.google.common.io.Resources
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(private val message_list: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     private val itemReceivedImg = 1
    private val itemReceivedMsg=2
    private lateinit var temp_timeStamp:Date
    private val simpleDateFormat=SimpleDateFormat("HH:mm aa")
    private val itemSentImg=3
    private var current_aimator:Animator?=null
    private var shortAnimationDuration:Int=0
    private val itemSentmsg = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            // infalte receive
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.received_image, parent, false)
            ReceivedImgViewholder(view)
        }
        else if(viewType==2)
        {
            val view:View=LayoutInflater.from(parent.context).inflate(R.layout.recieved,parent,false)
            ReceivedViewholder(view)
        }
        else if(viewType==3) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.sent_image, parent, false)
            SentImgViewholder(view)
        }
        else {
            val view:View=LayoutInflater.from(parent.context).inflate(R.layout.sent,parent,false)
            SentViewholder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = message_list[position]
        return if (FirebaseAuth.getInstance().uid.equals(currentMessage.senderId)) {
            if(currentMessage.message==null)
                itemSentImg
            else itemSentmsg
        } else {
            if(currentMessage.message==null)
                itemReceivedImg
            else itemReceivedMsg
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = message_list[position]
        temp_timeStamp=Date(currentMessage.timestamp)
        if (holder.javaClass == SentViewholder::class.java) // for sent view holder
        {
            holder as SentViewholder
            holder.sentmessage.text = currentMessage.message
            try{
                holder.timeview.text=simpleDateFormat.format(temp_timeStamp)
            }
            catch (e:Exception){}

        } else if(holder.javaClass==SentImgViewholder::class.java)
        {
            holder as SentImgViewholder
            try{
                holder.timeview.text=simpleDateFormat.format(temp_timeStamp)
            }
            catch (e:Exception){}
            if(currentMessage.ImageUrl==null)
                holder.sentimg.setImageResource(R.drawable.imgload)
            else
             ImgManager.loadImageIntoView(holder.sentimg,currentMessage.ImageUrl!!)
            holder.sentimg.setOnClickListener{
                shortAnimationDuration=it.context.resources.getInteger(android.R.integer.config_shortAnimTime)
                ZoomImageFromThumb(it as ImageView,it.context,currentMessage.ImageUrl.toString())            }
        }
        else if(holder.javaClass==ReceivedViewholder::class.java)
        {
            holder as ReceivedViewholder
            try{
                holder.timeview.text=simpleDateFormat.format(temp_timeStamp)
            }
            catch (e:Exception){}
            holder.receiverMessage.text = currentMessage.message
        }
        else {
            holder as ReceivedImgViewholder
            try{

                holder.timeview.text=simpleDateFormat.format(temp_timeStamp)
            }
            catch (e:Exception){}
            if(currentMessage.ImageUrl==null)
                holder.receiverimg.setImageResource(R.drawable.imgload)
            else
            ImgManager.loadImageIntoView(holder.receiverimg,currentMessage.ImageUrl!!)
            holder.receiverimg.setOnClickListener{
                shortAnimationDuration=it.context.resources.getInteger(android.R.integer.config_shortAnimTime)
                ZoomImageFromThumb(it as ImageView,it.context,currentMessage.ImageUrl.toString())
            }
        }
    }
    fun ZoomImageFromThumb(thumView: ImageView,context: Context ,imgUrl:String)
    {
        current_aimator?.cancel()
        val expandedImageView:ImageView=(context as Activity)
            .findViewById(R.id.zoomed_image_view)
        ImgManager.loadImageIntoView((expandedImageView as ImageView),imgUrl)

        val startBoundsInt= Rect()
        val finalBoundsInt= Rect()
        val globalOffset= Point()
        thumView.getGlobalVisibleRect(startBoundsInt)

        (context as Activity).findViewById<View>(R.id.chat_act_layout)
            .getGlobalVisibleRect(finalBoundsInt,globalOffset)
        startBoundsInt.offset(-globalOffset.x,globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x,globalOffset.y)
        val startBounds= RectF(startBoundsInt)
        val finalBounds= RectF(finalBoundsInt)

        val startscale:Float
        if((finalBounds.width()/finalBounds.height()>startBounds.width()/startBounds.height()))
        {
            startscale=startBounds.height()/finalBounds.height()
            val startwidth:Float=startscale*finalBounds.width()
            val deltawidth:Float=(startwidth-startBounds.width())/2
            startBounds.left-=deltawidth.toInt()
            startBounds.right+=deltawidth.toInt()
        }else
        {
            startscale=startBounds.width()/finalBounds.width()
            val startHeight:Float=startscale*finalBounds.height()
            val deltaHeight:Float=(startHeight-startBounds.height())/2f
            startBounds.top-=deltaHeight.toInt()
            startBounds.bottom+=deltaHeight.toInt()
        }
        thumView.alpha=0f
         expandedImageView.visibility=View.VISIBLE
        expandedImageView.pivotX=0f
        expandedImageView.pivotY=0f
        current_aimator= AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                    expandedImageView,
                    View.X,startBounds.left,finalBounds.left
                )
            ).apply {
                with(ObjectAnimator.ofFloat(expandedImageView,View.Y,startBounds.top,finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_X,startscale,1f))
                with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_Y,startscale,1f))
            }
            duration=shortAnimationDuration.toLong()
            interpolator= DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    current_aimator=null
                }

                override fun onAnimationCancel(animation: Animator?) {
                    current_aimator=null
                }
            })
            start()
        }
            expandedImageView.setOnClickListener {
            current_aimator?.cancel()

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            current_aimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startscale))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startscale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        current_aimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        current_aimator = null
                    }
                })
                start()
            }
        }
    }
    override fun getItemCount(): Int {
        return message_list.size
    }

    class SentViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val sentmessage = itemview.findViewById<TextView>(R.id.txt_sent_msg)!!
        val timeview=itemview.findViewById<TextView>(R.id.time_view)
    }
    class SentImgViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview=itemview.findViewById<TextView>(R.id.time_view)
        val sentimg = itemview.findViewById<ImageView>(R.id.SmessageImageView)!!
    }

    class ReceivedViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview=itemview.findViewById<TextView>(R.id.time_view)
        val receiverMessage = itemview.findViewById<TextView>(R.id.txt_rec_msg)!!
    }
    class ReceivedImgViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val timeview=itemview.findViewById<TextView>(R.id.time_view)
        val receiverimg = itemview.findViewById<ImageView>(R.id.RmessageImageView)!!
    }
}
