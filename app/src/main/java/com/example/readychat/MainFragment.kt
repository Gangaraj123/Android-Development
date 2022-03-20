package com.example.readychat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: user_adapter
    private lateinit var mauth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference
    private lateinit var loadGif: pl.droidsonroids.gif.GifImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mdbRef = FirebaseDatabase.getInstance().reference
        mauth = FirebaseAuth.getInstance()
        userList = ArrayList()
        loadGif = view.findViewById(R.id.loading)
        adapter = user_adapter(context, userList)
        userRecyclerView = view.findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.adapter = adapter
        mdbRef.child("user").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                userList.add(dataSnapshot.getValue(User::class.java)!!)
                adapter.notifyItemChanged(userList.size)
                if(loadGif.visibility==View.VISIBLE)
                    loadGif.visibility=View.GONE
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        userRecyclerView.visibility = View.VISIBLE
    }

}