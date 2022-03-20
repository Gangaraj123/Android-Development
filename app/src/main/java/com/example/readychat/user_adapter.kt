package com.example.readychat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.processNextEventInCurrentThread

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
        if(currentUser.profile_pic_uri!=null)
        {
            holder.Image.setImageURI(Uri.parse(currentUser.profile_pic_uri))
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("receiver_name", currentUser.name)
            intent.putExtra("receiver_uid", currentUser.uid)
            context?.startActivity(intent)
        }
    }

    class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textName= itemview.findViewById<TextView>(R.id.txtview)!!
        val Image=itemview.findViewById<ImageView>(R.id.profile)
    }
}
