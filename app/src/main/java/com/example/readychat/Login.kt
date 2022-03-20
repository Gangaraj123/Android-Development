package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText //declaring views
    private lateinit var edtPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: TextView
    private lateinit var mAuth: FirebaseAuth   // firebase atuh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
         mAuth = FirebaseAuth.getInstance()  //initialzing firebase authentication
         // initializing all views
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        loginButton = findViewById(R.id.btn_login)
        signupButton = findViewById(R.id.btn_Sign_up)

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val passwd = edtPassword.text.toString()
            if(TextUtils.isEmpty(email)||!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this,"Enter a valid email address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(passwd.length<5){
                Toast.makeText(this,"Password should be atleast 5 characters long",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email, passwd)
        }
    }

    private fun login(email: String, password: String) {

        // logging the current user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }
}