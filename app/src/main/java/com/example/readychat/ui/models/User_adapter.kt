package com.example.readychat.ui.models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.ChatActivity
import com.example.readychat.R
import com.example.readychat.ui.main.ImageZoom
import com.example.readychat.ui.main.ImgManager

class user_adapter(val context: Context?, val user_list: ArrayList<User>) :
    RecyclerView.Adapter<user_adapter.UserViewHolder>() {
     override fun getItemCount(): Int {
        return user_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = user_list[position]
        holder.textName.text = currentUser.name
        if(currentUser.profile_pic_url!=null)
        {
            ImgManager.loadImageIntoView(holder.Image,currentUser.profile_pic_url!!)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("receiver_name", currentUser.name)
            intent.putExtra("receiver_uid", currentUser.uid)
            if(currentUser.profile_pic_url!=null)
            intent.putExtra("profile_url",currentUser.profile_pic_url)
            context?.startActivity(intent)
        }
        holder.Image.setOnClickListener{
            val inent=Intent(context,ImageZoom::class.java)
            inent.putExtra("imgurl",currentUser.profile_pic_url)
            context?.startActivity(inent)
        }
    }

    class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textName= itemview.findViewById<TextView>(R.id.txtview)!!
        val Image=itemview.findViewById<ImageView>(R.id.profile)
    }
}
