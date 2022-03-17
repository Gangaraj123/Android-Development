package com.example.readychat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText //declaring views
    private lateinit var edtPassword: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_signup: TextView
    private lateinit var mAuth: FirebaseAuth   // firebase atuh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide() // hiding top bar
        mAuth = FirebaseAuth.getInstance()  //initialzing firebase authentication
        this.window.statusBarColor=this.resources.getColor(R.color.blue)
        // initializing all views
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btn_login = findViewById(R.id.btn_login)
        btn_signup = findViewById(R.id.btn_Sign_up)

        btn_signup.setOnClickListener {
            val intent: Intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val passwd = edtPassword.text.toString()
            login(email, passwd)
        }
    }

    private fun login(email: String, password: String) {

        // logging the current user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent: Intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }
}