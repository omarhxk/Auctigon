package com.example.htfapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Register request data class
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

// Register Activity
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val auth = FirebaseAuth.getInstance()
        val dbRef = FirebaseDatabase.getInstance().getReference()
        val registerButton = findViewById<Button>(R.id.RegisterButton)
        val usernameEditText = findViewById<EditText>(R.id.usernameText)
        val fNameEditText = findViewById<EditText>(R.id.firstNameText)
        val lNameEditText = findViewById<EditText>(R.id.lastNameText)
        val passwordEditText = findViewById<EditText>(R.id.passwordText)
        val emailEditText = findViewById<EditText>(R.id.emailText)
        val businessAcc = findViewById<CheckBox>(R.id.BusinessCheck)


        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val fname = fNameEditText.text.toString()
            val lname = lNameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val email = emailEditText.text.toString()

            if (username.isNotEmpty() && fname.isNotEmpty() && lname.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //storing user
                            val user = auth.currentUser
                            user?.sendEmailVerification()
                            val userId = user?.uid
                            val userInfo = HashMap<String, String>()
                            userInfo["Username"] = username
                            userInfo["First Name"] = fname
                            userInfo["Last Name"] = lname
                            if (userId != null) {
                                val userRef = dbRef.child("Users").child(userId)
                                userRef.setValue(userInfo)

                                val boolMap = HashMap<String, Boolean>()

                                boolMap["Business"] = businessAcc.isChecked
                                userRef.updateChildren(boolMap as Map<String, Any>)
                            }




                            if (businessAcc.isChecked) {
                                val intent = Intent(this, VerificationActivity::class.java)
                                startActivity(intent)
                            }
                            else {
                                Toast.makeText(this, "Please verify your email and then log in", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }

                        }
                        else {
                            Toast.makeText(this,"Sign up failed, please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
    }

}
