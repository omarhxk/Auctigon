package com.example.htfapp

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ImageUploader {

    private val baseUrl = "http://127.0.0.1:8000" // Replace with your ngrok URL or server URL

    fun uploadImage(imagePath: String) {
        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        val api = retrofit.create(ApiService::class.java)

        // Prepare the image file
        val file = File(imagePath)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Send the request
        val call = api.analyzeImage(imagePart)
        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Handle the successful response
                        val result = response.body()?.string()
                        Log.d("Response", result ?: "Empty response")
                    } else {
                        // Handle the error
                        Log.e("Error", "Request failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle the failure
                    Log.e("Error", "Request failed: ${t.message}")
                }
            })
        }
    }
}

private fun <T> Call<T>?.enqueue(callback: Callback<ResponseBody>) {

}
