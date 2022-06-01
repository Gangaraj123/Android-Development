package com.example.readychat.ui.slideshow

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.readychat.databinding.FragmentProfileBinding
import com.example.readychat.ui.main.ImgManager
import com.example.readychat.ui.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var profile_name_edit: EditText
    private lateinit var profile_about_edit: EditText
    private lateinit var profile_pic_edit: ImageView
    private lateinit var edit_btn: Button
    private lateinit var upload_btn: ImageButton
    private lateinit var profile_name: TextView
    private lateinit var profile_about: TextView
    private lateinit var mdbref: DatabaseReference
    private lateinit var curruser: User
    private var curr_img_uri: Uri? = null
    private lateinit var profile_email: TextView
    private lateinit var storageReference: StorageReference

    private lateinit var progressbar: ProgressBar
    private var imagelauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                curr_img_uri = it.data?.data
                profile_pic_edit.setImageURI(it.data?.data)
            }
        }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        profile_name_edit = binding.profileNameEdit
        profile_email = binding.profileEmail
        profile_about_edit = binding.profielAboutEdit
        upload_btn = binding.profilePicUploadBtn
        edit_btn = binding.profileEditBtn
        profile_pic_edit = binding.profilePicEdit
        progressbar = binding.progressBar
        profile_about = binding.profileAboutText
        profile_name = binding.profileNameText
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mdbref = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        mdbref.child("users").child(FirebaseAuth.getInstance().uid.toString()).get()
            .addOnSuccessListener {
                curruser = it.getValue(User::class.java)!!
                profile_name.text = curruser.name
                profile_about.text = curruser.about
                profile_email.text = curruser.email.toString()
            }
        mdbref.child("users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    curruser = snapshot.getValue(User::class.java)!!
                    profile_about_edit.visibility = View.GONE
                    profile_name_edit.visibility = View.GONE
                    profile_name.text = curruser.name
                    profile_about.text = curruser.about
                    profile_email.text = curruser.email.toString()
                    if (curruser.profile_pic_url != null)
                        ImgManager.LoadProfileIntoView(
                            profile_pic_edit,
                            curruser.profile_pic_url,
                            edit_btn,
                            progressbar,
                            profile_name,
                            profile_about
                        )
                    else {
                        progressbar.visibility = View.GONE
                        edit_btn.text = "Edit"
                        edit_btn.visibility = View.VISIBLE
                        profile_about.visibility = View.VISIBLE
                        profile_name.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        edit_btn.setOnClickListener(View.OnClickListener {
            if (edit_btn.text == "Edit") {
                edit_btn.text = "save"
                profile_name_edit.setText(curruser.name)
                profile_about_edit.setText(curruser.about)
                profile_name.visibility = View.GONE
                profile_about.visibility = View.GONE
                profile_name_edit.visibility = View.VISIBLE
                profile_about_edit.visibility = View.VISIBLE
                upload_btn.visibility = View.VISIBLE
            } else if (edit_btn.text == "save") {
                edit_btn.visibility = View.GONE
                upload_btn.visibility = View.GONE
                progressbar.visibility = View.VISIBLE
                val user = User()
                user.name = profile_name_edit.text.toString()
                user.email = curruser.email
                user.uid = curruser.uid
                user.about = profile_about_edit.text.toString()
                user.profile_pic_url = curruser.profile_pic_url
                ImgManager.putProfile_In_Storage(
                    FirebaseStorage.getInstance().reference,
                    curr_img_uri,
                    user,
                    mdbref
                )
            }
        })
        upload_btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagelauncher.launch(intent)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}