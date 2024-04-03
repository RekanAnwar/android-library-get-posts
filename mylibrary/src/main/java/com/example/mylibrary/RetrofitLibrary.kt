package com.example.mylibrary

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

object OkHttpApiClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val client = OkHttpClient()

    fun getPosts(callback: (List<Post>?, Throwable?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL + "posts")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    val posts = json?.let {
                        Gson().fromJson(it, Array<Post>::class.java).toList()
                    }
                    callback(posts, null)
                } else {
                    callback(null, IOException("Failed to fetch posts: ${response.code()}"))
                }
            }
        })
    }
}

object PostPrinter {
    fun printPosts() {
        OkHttpApiClient.getPosts { posts, error ->
            if (posts != null) {
                posts.forEach {
                    Log.d("Post", "Title: ${it.title}, Body: ${it.body}")
                }
            } else {
                Log.e("Error", "Failed to fetch posts: ${error?.message}")
            }
        }
    }
}
