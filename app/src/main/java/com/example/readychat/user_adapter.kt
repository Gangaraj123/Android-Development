package com.example.readychat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class user_adapter(val context: Context, val user_list: ArrayList<user>) :
    RecyclerView.Adapter<user_adapter.UserViewHolder>() {

    override fun getItemCount(): Int {
        return user_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current_user = user_list[position]
        holder.textName.text = current_user.name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, chatActivity::class.java)
            intent.putExtra("name", current_user.name)
            intent.putExtra("uid", current_user.uid)

            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textName = itemview.findViewById<TextView>(R.id.txtview)
    }
}
