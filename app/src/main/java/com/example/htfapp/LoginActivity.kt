package com.example.htfapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
                            if (uid != null) {
                                dbRef.child("Users").child(uid).child("Business")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val businessAcc = snapshot.getValue(Boolean::class.java)
                                            if (businessAcc == false) {
                                                val intent = Intent(
                                                    this@LoginActivity,
                                                    UserHomeScreenActivity::class.java
                                                )
                                                startActivity(intent)
                                            } else {
                                                val intent = Intent(
                                                    this@LoginActivity,
                                                    BusinessHomeScreenActivity::class.java
                                                )
                                                startActivity(intent)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(this@LoginActivity, "Couldnt find user", Toast.LENGTH_SHORT).show()
                                        }
                                    })
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