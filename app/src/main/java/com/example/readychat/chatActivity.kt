package com.example.readychat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class chatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: Message_Adapter
    private lateinit var messaglist: ArrayList<Message>
    private lateinit var mdbref: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val name = intent.getStringExtra("name")
        this.window.statusBarColor=this.resources.getColor(R.color.blue)
        val receiver_uid = intent.getStringExtra("uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = receiver_uid + senderuid
        receiverRoom = senderuid + receiver_uid
        supportActionBar?.title = name
        supportActionBar?.setBackgroundDrawable(AppCompatResources.getDrawable(this,R.drawable.action_bar_bg))
        chatRecyclerView = findViewById(R.id.chat_recyleview)
        messageBox = findViewById(R.id.message_box)
        sendButton = findViewById(R.id.send_btn)
        mdbref = FirebaseDatabase.getInstance().reference
        messaglist = ArrayList()
        messageAdapter = Message_Adapter(this, messaglist)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // adding data to recyclerview
        mdbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messaglist.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messaglist.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.smoothScrollToPosition(messaglist.size) // making recycler view to scrool to bottom
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        // adding message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            if(Is_blank(message)) return@setOnClickListener
            val msg_obj = senderuid?.let { it1 -> Message(message.trim(), it1) }
            mdbref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(msg_obj).addOnSuccessListener {
                    mdbref.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(msg_obj)
                }
            messageBox.setText("")

        }
    }
    private fun Is_blank(msg:String):Boolean
    {
        for(i in 0..msg.length-1)
        {
            if(msg[i]!=' ')
                return false

        }
        return true
    }
}