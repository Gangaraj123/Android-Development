package com.example.readychat

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.Message
import com.example.readychat.ui.models.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*


class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var myDatabaseReference: DatabaseReference
    private lateinit var receiverName: TextView
    private lateinit var source:Uri
    private lateinit var dest:Uri
    public var current_aimator:Animator?=null
     public var shortAnimationDuration:Int=0
     private var imagelauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== Activity.RESULT_OK)
        {
              source=it.data?.data!!
            val destUri=StringBuilder(UUID.randomUUID().toString()).append(".jpg")
                .toString()
            val options=UCrop.Options()
            UCrop.of(source,Uri.fromFile(File(cacheDir,destUri)))
                .withOptions(options)
                .withAspectRatio(0F,0F)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000,2000)
                .start(this@ChatActivity)

//            val intent=Intent(this@ChatActivity,CropperActivity::class.java)
//            intent.putExtra("Data",source.toString())



        }
    }

    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var profile:ImageView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
         val name = intent.getStringExtra("receiver_name")
        val url=intent.getStringExtra("profile_url")
        shortAnimationDuration=resources.getInteger(android.R.integer.config_shortAnimTime)
        receiverName = findViewById(R.id.receiver_chat_title)
        profile=findViewById(R.id.profile)
        if(url!=null)
        ImgManager.loadImageIntoView(profile,url)
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
        val linearLayoutManager = LinearLayoutManager(this)
         linearLayoutManager.stackFromEnd=true
        chatRecyclerView.layoutManager=linearLayoutManager
        chatRecyclerView.adapter = messageAdapter
        

        // adding data to recyclerview
        myDatabaseReference.child("chats").child(senderRoom!!).child(   "messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot:     DataSnapshot, prevChildKey: String?) {
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
        sendButton.setOnClickListener qq{
            val message = messageBox.text.toString()
            if (message.trim().isEmpty()) return@setOnClickListener
            val messageObject = senderuid?.let { it1 -> Message(message.trim(), it1,null)}
            myDatabaseReference.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    myDatabaseReference.child("chats").child(receiverRoom!!).child("messages")
                        .push()
                        .setValue(messageObject)
                        .addOnSuccessListener {
                            myDatabaseReference.child("users").child(receiveruid!!).child("friends_list")
                                .child(senderuid!!).setValue(System.currentTimeMillis())
                            myDatabaseReference.child("users").child(senderuid!!).child("friends_list")
                                .child(receiveruid!!).setValue(System.currentTimeMillis())
                        }
//                    Log.d("abba","Success")
                }.addOnFailureListener()
                {
//                    Log.d("abba","Failed to send message")
                }
            messageBox.setText("")

        }

        messageBox.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK&&requestCode==UCrop.REQUEST_CROP)
        {
            val resultUri:Uri?=  data?.let { UCrop.getOutput(it) }
                        if (resultUri !=null && senderRoom!=null && receiverRoom!=null)
                ImgManager.putImageInStorage(
                    FirebaseStorage.getInstance().reference, resultUri,
                    senderRoom!!, receiverRoom!!,myDatabaseReference)
        }
    }

}