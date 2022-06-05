package com.example.readychat.ui.models

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.ChatActivity
import com.example.readychat.R
import com.example.readychat.ui.main.ImageZoom
import com.example.readychat.ui.main.ImgManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

private val mdbref = FirebaseDatabase.getInstance().reference

class user_adapter(val context: Context?, val user_list: ArrayList<User>) :
    RecyclerView.Adapter<user_adapter.UserViewHolder>() {
    override fun getItemCount(): Int {
        return user_list.size
    }

    private lateinit var temp_timeStamp: Date
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yy  hh:mm aa")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = user_list[position]
        holder.textName.text = currentUser.name
        mdbref.child("users").child(FirebaseAuth.getInstance().uid!!)
            .child("friends_list").child(currentUser.uid!!).child("time")
            .get().addOnSuccessListener {
                temp_timeStamp = Date(it.getValue(Long::class.java)!!)
                holder.last_msg_time.text = simpleDateFormat.format(temp_timeStamp)
            }
            .addOnFailureListener {
                holder.last_msg_time.text = ""
            }

            ImgManager.loadImageIntoView(holder.Image, currentUser.profile_pic_url,true)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            mdbref.child("users").child(FirebaseAuth.getInstance().uid!!)
                .child("friends_list").child(currentUser.uid!!)
                .child("unread").setValue(false)
            intent.putExtra("receiver_name", currentUser.name)
            intent.putExtra("receiver_uid", currentUser.uid)
            if (currentUser.profile_pic_url != null)
                intent.putExtra("profile_url", currentUser.profile_pic_url)
            context?.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener(OnLongClickListener {
            if (context != null) {
                val cont2=ContextThemeWrapper(context,R.style.AppTheme2)
                MaterialAlertDialogBuilder(cont2)
                    .setTitle("Warning")
                    .setMessage("Do you want to delete ${currentUser.name} from your friend list? ")
                    .setNegativeButton("NO") { dialog, which ->
                            dialog.dismiss()
                    }
                    .setPositiveButton("YES") { dialog, which ->
                        // Respond to positive button press
                        MaterialAlertDialogBuilder(cont2)
                            .setTitle("Warning")
                            .setMessage("Are you sure ?")
                            .setNegativeButton("CANCEL"){d,w->
                                d.dismiss()
                            }
                            .setPositiveButton("YES"){d,w->
                                mdbref.child("users").child(FirebaseAuth.getInstance().uid!!)
                                    .child("friends_list").child(currentUser.uid!!)
                                    .removeValue()
                                    .addOnSuccessListener {
                                        mdbref.child("users").child(currentUser.uid!!)
                                            .child("friends_list").child(FirebaseAuth.getInstance().uid!!)
                                            .removeValue()
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(context,"Failed to delete",Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .show()
                    }
                    .show()
            }
            true
        })

        holder.Image.setOnClickListener {
            val inent = Intent(context, ImageZoom::class.java)
            inent.putExtra("imgurl", currentUser.profile_pic_url)
            context?.startActivity(inent)
        }
        mdbref.child("users").child(FirebaseAuth.getInstance().uid!!)
            .child("friends_list").child(currentUser.uid!!)
            .child("unread").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.getValue(Boolean::class.java)!!) {
                        holder.unread_mark.visibility = View.VISIBLE
                    } else
                        holder.unread_mark.visibility = View.GONE
                }
            })
    }

    class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textName = itemview.findViewById<TextView>(R.id.txtview)!!
        val Image = itemview.findViewById<ImageView>(R.id.profile)
        val unread_mark = itemview.findViewById<ImageView>(R.id.unread_dot)
        val last_msg_time = itemview.findViewById<TextView>(R.id.last_msg_time)
    }
}
