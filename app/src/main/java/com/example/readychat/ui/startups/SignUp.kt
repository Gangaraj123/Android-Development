package com.example.readychat.ui.startups

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.readychat.MainActivity
import com.example.readychat.R
import com.example.readychat.ui.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.sign

class SignUp : AppCompatActivity() {
    private lateinit var edtEmail: EditText //declaring views
    private lateinit var edtPassword: EditText
    private lateinit var name: EditText
    private lateinit var signupButton: com.flod.loadingbutton.LoadingButton
    private lateinit var mAuth: FirebaseAuth   // firebase atuh
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()  //initialzing firebase authentication

        // initializing all views
        edtEmail = findViewById(R.id.edt_email)
        name = findViewById(R.id.edt_name)
        edtPassword = findViewById(R.id.edt_password)
        signupButton = findViewById(R.id.btn_Sign_up)

        signupButton.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = name.text.toString().trim()
            if (name.length < 3) {
                Toast.makeText(this, "name should be atleast 3 characters long", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 5) {
                Toast.makeText(
                    this,
                    "Password should be atleast 5 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            signup(name, email, password)
        }
    }

    private fun signup(name: String, email: String, password: String) {
        signupButton.start()
        // creating new user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!)
                    val intent= Intent(this@SignUp, MainActivity::class.java)
                    signupButton.complete(true)
                    finish()
                    startActivity(intent)
                } else {
                    Log.d("", "Can't create account, ")
                    signupButton.complete(false)
                    Toast.makeText(
                        this@SignUp,
                        "Email already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("users").child(uid).child("user_details").setValue(User(name, email, uid))
    }

}