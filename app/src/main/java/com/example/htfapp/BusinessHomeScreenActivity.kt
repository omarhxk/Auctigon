package com.example.htfapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BusinessHomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_home_screen)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val scroller = findViewById<ScrollView>(R.id.scroller)
        val newListingButton = findViewById<Button>(R.id.newButton)
        val linearlayout0 = LinearLayout(this@BusinessHomeScreenActivity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }

        val userListingsRef =
            userId?.let { FirebaseDatabase.getInstance().getReference().child("Users").child(it).child("Listings") }
        userListingsRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    val listingRef = key?.let {
                        FirebaseDatabase.getInstance().getReference().child("Listings").child(
                            it
                        )
                    }

                    val linearlayout = LinearLayout(this@BusinessHomeScreenActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                        orientation = LinearLayout.HORIZONTAL
                    }

                    //creating the corresponding image
                    //val imageview = ImageView(this@BusinessHomeScreenActivity).apply {
                    //    layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx)
                   //     scaleType = ImageView.ScaleType.CENTER_CROP
                    //    setImageResource(R.drawable.logo)
                    //}


                   // linearlayout.addView(imageview)

                    listingRef?.child("Product Name")?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val buttonText = snapshot.getValue(String::class.java)
                            //creating the corresponding button and title
                            val button = Button(this@BusinessHomeScreenActivity).apply {

                                text = buttonText
                                setTextColor(Color.BLACK)
                                setBackgroundColor(Color.TRANSPARENT)
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)

                                setOnClickListener {
                                    intent = Intent(
                                        this@BusinessHomeScreenActivity,
                                        productViewActivity::class.java
                                    )
                                    intent.putExtra("Product", key)
                                    startActivity(intent)
                                }
                            }

                            linearlayout.addView(button)

                            linearlayout0.addView(linearlayout)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@BusinessHomeScreenActivity, "drerr", Toast.LENGTH_SHORT).show()

                        }
                    })


                } }
                else {
                    Toast.makeText(this@BusinessHomeScreenActivity, "No current listings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BusinessHomeScreenActivity, "drerr", Toast.LENGTH_SHORT).show()
            }
        })

        scroller.addView(linearlayout0)

        newListingButton.setOnClickListener {
            intent = Intent(this, sellProductActivity::class.java)
            startActivity(intent)
        }

    }

}