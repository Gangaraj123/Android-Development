package com.example.readychat.ui.Requests

import android.icu.util.Freezable
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readychat.databinding.FragmentRequestsBinding
import com.example.readychat.ui.models.Friend_Request
import com.example.readychat.ui.models.Request_Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mdbref:DatabaseReference
    private lateinit var requestRecyclerView: RecyclerView
    private lateinit var req_adapter:Request_Adapter
    private lateinit var req_list:ArrayList<Friend_Request>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentRequestsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        req_list=ArrayList ()
        mdbref=FirebaseDatabase.getInstance().reference
        req_adapter= Request_Adapter(context,req_list)
        requestRecyclerView=binding.requestRecyclerview
        requestRecyclerView.layoutManager=LinearLayoutManager(context)
        requestRecyclerView.adapter=req_adapter
        mdbref.child("users").child(FirebaseAuth.getInstance().uid!!)
            .child("friend_requests").addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    req_list.clear()
                    for(x in snapshot.children)
                        req_list.add(x.getValue(Friend_Request::class.java)!!)
                    Toast.makeText(context,"You have "+req_list.size.toString()+" requests",Toast.LENGTH_SHORT).show()
                    req_adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}