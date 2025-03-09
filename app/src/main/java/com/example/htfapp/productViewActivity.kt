package com.example.htfapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class productViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)

        val img = findViewById<ImageView>(R.id.productImage)
        val name = findViewById<TextView>(R.id.productName)
        val buyNowPrice = findViewById<TextView>(R.id.buyNowPrice)
        val buyButton = findViewById<Button>(R.id.buyButton)
        val bidButton = findViewById<Button>(R.id.bidButton)
        val backButton = findViewById<Button>(R.id.backButton)

        val productKey = intent.getStringExtra("Product")

        val productRef =
            productKey?.let { FirebaseDatabase.getInstance().reference.child("Products").child(it) }

        if (productRef != null) {
            productRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name.text = snapshot.child("Product Name").getValue(String::class.java)
                    buyNowPrice.text = snapshot.child("Buy Now Price").getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeScreenActivity::class.java)
            startActivity(intent)
        }

    }

    fun loadImageFromFirebase(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)  // Use imageView's context
            .load(imageUrl)            // Load the image from URL
            .into(imageView)           // Set it to the ImageView
    }
}