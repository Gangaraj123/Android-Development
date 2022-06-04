package com.example.readychat.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.databinding.FragmentHomeBinding
import com.example.readychat.ui.models.User
import com.example.readychat.ui.models.user_adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: user_adapter
    private lateinit var mauth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference
    private lateinit var loadGif: pl.droidsonroids.gif.GifImageView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mdbRef = FirebaseDatabase.getInstance().reference
        mauth = FirebaseAuth.getInstance()
        userList = ArrayList()
        loadGif = binding.loading
        adapter = user_adapter(context, userList)
        userRecyclerView = binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("abba", "But reached here")
        mdbRef.child("users").child(mauth.uid!!).child("friends_list").orderByValue()
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (loadGif.visibility == View.VISIBLE)
                        loadGif.visibility = View.GONE
                      mdbRef.child("users").child(snapshot.key!!).child("user_details")
                        .get().addOnSuccessListener {
                            userList.add(0, it.getValue(User::class.java)!!)
                            adapter.notifyItemInserted(0)
                        }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                    mdbRef.child("users").child(snapshot.key!!).child("user_details")
                        .get().addOnSuccessListener {
                            val temp = it.getValue(User::class.java)
                            if (userList[0].uid != temp?.uid) {

                                for (index in 0..userList.size) {
                                    if (userList[index].uid == temp?.uid) {
                                        userList.removeAt(index)
                                        adapter.notifyItemRemoved(index)
                                        if (temp != null) {
                                            userList.add(0, temp)
                                            adapter.notifyItemInserted(0)
                                        }
                                        break
                                    }
                                }
                            }

                        }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    mdbRef.child("users").child(snapshot.key!!).child("user_details")
                        .get().addOnSuccessListener {
                            val removeduser = it.getValue(User::class.java)!!
                            val index = getIndex(removeduser)
                            if (index != -1) {
                                userList.removeAt(index)
                                adapter.notifyItemRemoved(0)
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }
            })
    }

    fun getIndex(user: User): Int {
        for (i in 0..userList.size) {
            if (userList[i].uid == user.uid)
                return i
        }
        return -1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}