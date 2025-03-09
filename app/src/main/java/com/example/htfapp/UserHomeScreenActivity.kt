package com.example.htfapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class UserHomeScreenActivity : AppCompatActivity() {
    // Convert 85dp to pixels
    //val sizeInPx = TypedValue.applyDimension(
    //    TypedValue.COMPLEX_UNIT_DIP, 85f, resources.displayMetrics
   // ).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home_screen)

        val scroller = findViewById<ScrollView>(R.id.scroller)
        val linearlayout0 = LinearLayout(this@UserHomeScreenActivity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }
        val listingsRef = FirebaseDatabase.getInstance().reference.child("Listings")
        listingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    val linearlayout = LinearLayout(this@UserHomeScreenActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.HORIZONTAL
                    }

                    //product image
                    //val imageview = ImageView(this@UserHomeScreenActivity).apply {
                        //layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx)
                       /// scaleType = ImageView.ScaleType.CENTER_CROP
                      //  setImageResource(R.drawable.logo)
                    //}



                   // linearlayout.addView(imageview)

                    //product text/button
                    val button = Button(this@UserHomeScreenActivity).apply {

                        text = childSnapshot.child("Product Name").getValue(String::class.java)
                        setTextColor(Color.BLACK)
                        setBackgroundColor(Color.TRANSPARENT)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)

                        setOnClickListener {
                            intent = Intent(this@UserHomeScreenActivity, productViewActivity::class.java)
                            intent.putExtra("Product", key)
                            startActivity(intent)
                        }
                    }

                    linearlayout.addView(button)

                    val price = TextView(this@UserHomeScreenActivity).apply {
                        text = childSnapshot.child("Buy Now Price").getValue(String::class.java)
                        setTextColor(Color.BLACK) // Set text color to black
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    linearlayout.addView(price)

                    linearlayout0.addView(linearlayout)
                }
            }

            else {
                Toast.makeText(this@UserHomeScreenActivity, "No current listings", Toast.LENGTH_SHORT).show()
            }}


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserHomeScreenActivity, "drerr", Toast.LENGTH_SHORT).show()
            }
        })

        scroller.addView(linearlayout0)


    }

}