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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.auth.FirebaseAuth


class sellProductActivity : AppCompatActivity() {

    private lateinit var pickImage: ActivityResultLauncher<String>

    val REQUEST_CODE_READ_STORAGE = 1001 // For requesting permissions on older versions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sell_product)

        val dbRef = FirebaseDatabase.getInstance().getReference()
        val productName = findViewById<EditText>(R.id.productNameText)
        val productId = findViewById<EditText>(R.id.productCodeText)
        val description = findViewById<EditText>(R.id.descriptionText)
        val startingPrice = findViewById<EditText>(R.id.startingPriceText)
        val buyPrice = findViewById<EditText>(R.id.buyNowText)
        val listButton = findViewById<Button>(R.id.ListButton)
        val errorMsg = findViewById<TextView>(R.id.errorMsg)
        val m = HashMap<String, String>()

        pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val imageUrl = uploadImageToFirebase(it)
                m["imageUrl"] = imageUrl
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Check for permissions before triggering image picker
        if (hasStoragePermission()) {
            setupUploadButton()
        } else {
            requestStoragePermission()
        }

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
                    dbRef.child(key).child("Clicks").setValue(0)
                    dbRef.child(key).updateChildren(m as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Listing Published!", Toast.LENGTH_SHORT).show()
                            intent = Intent(this, BusinessHomeScreenActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Listing Failed", Toast.LENGTH_SHORT).show()
                        }
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    dbRef.child("Users").child(userId).child("Listings").push().setValue(key)
                }
            }
            else {
                errorMsg.visibility = View.VISIBLE
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        // Check if the app has permission for reading storage (for Android 13 and below)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, check for READ_MEDIA_IMAGES permission
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            // For older versions, check for READ_EXTERNAL_STORAGE permission
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ requesting READ_MEDIA_IMAGES
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_READ_STORAGE)
        } else {
            // For older versions requesting READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, setup the button
                setupUploadButton()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUploadButton() {
        // Setup the Upload Image button
        val pickImageButton: Button = findViewById(R.id.uploadButton)
        pickImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun uploadImageToFirebase(uri: Uri) : String {
        // Upload image to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child("images/${uri.lastPathSegment}")
        var imageUrl = ""
        storageReference.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()

                // Get the download URL of the uploaded image and store it in Realtime Database
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        return imageUrl
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        // Save the image URL to Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("images")
        val imageId = databaseReference.push().key // Automatically generate a new child ID
        imageId?.let {
            databaseReference.child(it).setValue(imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image URL saved to database!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save image URL", Toast.LENGTH_SHORT).show()
                }
        }
    }
}