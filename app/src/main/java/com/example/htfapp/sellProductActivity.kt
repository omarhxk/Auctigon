package com.example.htfapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class sellProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sell_product)

        val dbRef = FirebaseDatabase.getInstance().getReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val productName = findViewById<EditText>(R.id.productNameText)
        val productId = findViewById<EditText>(R.id.productCodeText)
        val description = findViewById<EditText>(R.id.descriptionText)
        val startingPrice = findViewById<EditText>(R.id.startingPriceText)
        val buyPrice = findViewById<EditText>(R.id.buyNowText)
        val listButton = findViewById<Button>(R.id.ListButton)
        val errorMsg = findViewById<TextView>(R.id.errorMsg)
        val m = HashMap<String, String>()
        val assessButton = findViewById<Button>(R.id.qualityButton)


        listButton.setOnClickListener {
            val name = productName.text.toString()
            val id = productId.text.toString()
            val desc = description.text.toString()
            val starting = startingPrice.text.toString()
            val buy = buyPrice.text.toString()

            if (name.isNotEmpty() && id.isNotEmpty() && desc.isNotEmpty() && starting.isNotEmpty() && buy.isNotEmpty()) {
                errorMsg.visibility = View.INVISIBLE
                m["Product Name"] = name;
                m["Product Id"] = id;
                m["Description"] = desc;
                m["Starting Price"] = starting
                m["Buy Now Price"] = buy

                val key = dbRef.child("Listings").push().key
                if (key != null) {
                    userId?.let { it1 ->
                        dbRef.child(it1).child("Listings").child(key)
                            .updateChildren(m as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Listing Published!", Toast.LENGTH_SHORT)
                                    .show()
                                val userId = FirebaseAuth.getInstance().currentUser?.uid
                                if (userId != null) {
                                    dbRef.child("Users").child(userId).child("Listings").push()
                                        .setValue(key)
                                }
                                intent = Intent(this, BusinessHomeScreenActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Listing Failed", Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }


            else {
                errorMsg.visibility = View.VISIBLE
            }
        }

        assessButton.setOnClickListener {
            intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
        }
    }


}