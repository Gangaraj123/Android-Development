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


/**
 * A simple [Fragment] subclass.
 * Use the [Main_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Main_Fragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var user_list: ArrayList<user>
    private lateinit var adapter: user_adapter
    private lateinit var mauth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference
    private lateinit var load_gif: pl.droidsonroids.gif.GifImageView

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
        user_list = ArrayList()
        load_gif = view.findViewById(R.id.loading)
        adapter = user_adapter(context,user_list)
        userRecyclerView = view.findViewById<RecyclerView>(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.adapter = adapter
        mdbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user_list.clear()
                for (postSnapshot in snapshot.children) {
                    val current_user = postSnapshot.getValue(user::class.java)
                    if (mauth.currentUser?.uid != current_user?.uid)
                        user_list.add(current_user!!)
                }
                adapter.notifyDataSetChanged()
                load_gif.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        userRecyclerView.visibility = View.VISIBLE
    }

}