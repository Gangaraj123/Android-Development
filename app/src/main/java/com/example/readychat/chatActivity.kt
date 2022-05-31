package com.example.readychat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.databinding.ActivityChatBinding
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var myDatabaseReference: DatabaseReference
    private lateinit var receiverName: TextView
    private var imgManager=ImgManager()
    private var imagelauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== Activity.RESULT_OK)
        {
            val x=it.data?.data
            if (x != null) {
                senderRoom?.let { it1 ->
                    receiverRoom?.let { it2 ->
                        imgManager.putImageInStorage(FirebaseStorage.getInstance().reference,x,
                            it1, it2,myDatabaseReference
                        )
                    }
                }
            }
        }
    }

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
         val name = intent.getStringExtra("receiver_name")

        receiverName = findViewById(R.id.receiver_chat_title)
        receiverName.text = name
        val receiveruid = intent.getStringExtra("receiver_uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid

//        Log.d("abba","received id = "+receiveruid.toString())
        senderRoom = receiveruid + senderuid
        receiverRoom = senderuid + receiveruid

        chatRecyclerView = findViewById(R.id.chat_recyclerview)
        messageBox = findViewById(R.id.message_box)
        sendButton = findViewById(R.id.send_btn)
        myDatabaseReference = FirebaseDatabase.getInstance().reference
        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter


        // adding data to recyclerview
        myDatabaseReference.child("chats").child(senderRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    messageList.add(dataSnapshot.getValue(Message::class.java)!!)
                    messageAdapter.notifyItemChanged(messageList.size)
                    chatRecyclerView.scrollToPosition(messageList.size-1)
                 }
                override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                }
                override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        // adding message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            if (message.trim().isEmpty()) return@setOnClickListener
            val messageObject = senderuid?.let { it1 -> Message(message.trim(), it1) }
            myDatabaseReference.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    myDatabaseReference.child("chats").child(receiverRoom!!).child("messages")
                        .push()
                        .setValue(messageObject)
//                    Log.d("abba","Success")
                }.addOnFailureListener()
                {
//                    Log.d("abba","Failed to send message")
                }
            messageBox.setText("")

        }

        messageBox.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            val intent=Intent(Intent.ACTION_PICK)

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= messageBox.right - messageBox.compoundDrawables
                        .get(DRAWABLE_RIGHT).bounds.width()
                ) {
                    val intent=Intent(Intent.ACTION_PICK)
                    intent.type="image/*"
                    imagelauncher.launch(intent)
                }
            }
            false
        }

        )

        findViewById<LinearLayout>(R.id.back_and_profile).setOnClickListener {
            finish()
            return@setOnClickListener
        }
    }

}