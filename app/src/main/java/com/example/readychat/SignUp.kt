package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var edtEmail: EditText //declaring views
    private lateinit var edtPassword: EditText
    private lateinit var name: EditText
    private lateinit var btn_signup: Button
    private lateinit var mAuth: FirebaseAuth   // firebase atuh
    private lateinit var mDbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()
        this.window.statusBarColor=this.resources.getColor(R.color.blue)
        mAuth = FirebaseAuth.getInstance()  //initialzing firebase authentication

        // initializing all views
        edtEmail = findViewById(R.id.edt_email)
        name = findViewById(R.id.edt_name)
        edtPassword = findViewById(R.id.edt_password)
        btn_signup = findViewById(R.id.btn_Sign_up)

        btn_signup.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = name.text.toString().trim()
            signup(name,email, password)
        }
    }

    private fun signup(name: String, email: String, password: String) {
        // creating new user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent: Intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Log.d("", "Can't create account, ")
                    Toast.makeText(
                        this@SignUp,
                        "Sorry, some error occured or maybe email already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
      mDbRef=FirebaseDatabase.getInstance().getReference()
      mDbRef.child("user").child(uid).setValue(user(name,email,uid))
    }

}