package com.example.htfapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class UserHomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home_screen)

        val trendingItem1 = findViewById<LinearLayout>(R.id.trendingItem1)
        val trendingItem2 = findViewById<LinearLayout>(R.id.trendingItem2)
        val trendingItem3 = findViewById<LinearLayout>(R.id.trendingItem3)
        val relevantItem1 = findViewById<LinearLayout>(R.id.relevantItem1)
        val relevantItem2 = findViewById<LinearLayout>(R.id.relevantItem2)
        val relevantItem3 = findViewById<LinearLayout>(R.id.relevantItem3)

        val trendingItem1name = findViewById<Button>(R.id.trendingItem1Name)
        val trendingItem2name = findViewById<Button>(R.id.trendingItem2name)
        val trendingItem3name = findViewById<Button>(R.id.trendingItem3name)
        val relevantItem1name = findViewById<Button>(R.id.relevantItem1name)
        val relevantItem2name = findViewById<Button>(R.id.relevantItem2name)
        val relevantItem3name = findViewById<Button>(R.id.relevantItem3name)

        val trendingItem1img = findViewById<ImageView>(R.id.trendingItem1img)
        val trendingItem2img = findViewById<ImageView>(R.id.trendingItem2img)
        val trendingItem3img = findViewById<ImageView>(R.id.trendingItem3img)
        val relevantItem1img = findViewById<ImageView>(R.id.relevantItem1img)
        val relevantItem2img = findViewById<ImageView>(R.id.relevantItem2img)
        val relevantItem3img = findViewById<ImageView>(R.id.relevantItem3img)

        val trendingItem1price = findViewById<TextView>(R.id.trendingItem1price)
        val trendingItem2price = findViewById<TextView>(R.id.trendingItem2price)
        val trendingItem3price = findViewById<TextView>(R.id.trendingItem3price)
        val relevantItem1price = findViewById<TextView>(R.id.relevantItem1price)
        val relevantItem2price = findViewById<TextView>(R.id.relevantItem2price)
        val relevantItem3price = findViewById<TextView>(R.id.relevantItem3price)

        val listings = arrayListOf(relevantItem1, relevantItem2, relevantItem3, trendingItem1, trendingItem2, trendingItem3)
        val imgs = arrayListOf(relevantItem1img, relevantItem2img, relevantItem3img, trendingItem1img, trendingItem2img, trendingItem3img)
        val names = arrayListOf(relevantItem1name, relevantItem2name, relevantItem3name, trendingItem1name, trendingItem2name, trendingItem3name)
        val prices = arrayListOf(relevantItem1price, relevantItem2price, relevantItem3price, trendingItem1price, trendingItem2price, trendingItem3price)

        val listingsRef = FirebaseDatabase.getInstance().getReference().child("Listings")
        listingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val len = snapshot.children.count()
                var i = 0
                for (childSnapshot in snapshot.children) {
                    listings[i].visibility = View.VISIBLE
                    childSnapshot.child("imageUrl").getValue(String::class.java)
                        ?.let { loadImageFromFirebase(imgs[i], it) }
                    names[i].text = childSnapshot.child("Product Name").getValue(String::class.java)
                    prices[i].text = "$" + childSnapshot.child("Buy Now Price").getValue(String::class.java)
                    i++
                }

                while (i < len) {
                    listings[i].visibility = View.GONE
                    i++
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun loadImageFromFirebase(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)  // Use imageView's context
            .load(imageUrl)            // Load the image from URL
            .into(imageView)           // Set it to the ImageView
    }
}