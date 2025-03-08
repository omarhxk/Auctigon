package com.example.htfapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


// Login Activity
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailEditText = findViewById<EditText>(R.id.emailText)
        val passwordEditText = findViewById<EditText>(R.id.passwordText)
        val signUpButton = findViewById<Button>(R.id.RegisterButton)

        val auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid

                            val dbRef = FirebaseDatabase.getInstance().reference
                            uid?.let { it1 -> dbRef.child("Users").child(it1).child("Business").get()
                                .addOnSuccessListener { snapshot ->
                                    val businessAcc = snapshot.getValue(Boolean::class.java)
                                    if (businessAcc == true) {
                                        val intent = Intent(this, UserHomeScreenActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else {
                                        val intent = Intent(this, BusinessHomeScreenActivity::class.java)
                                        startActivity(intent)
                                    }
                                 }

                            }
                        }
                    }
            }

        }

        signUpButton.setOnClickListener {
            intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}