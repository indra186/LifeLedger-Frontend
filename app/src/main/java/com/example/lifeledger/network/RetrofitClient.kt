package com.example.lifeledger.network

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.21.135.247/lifeledger/"

    private var authToken: String? = null
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        authToken = prefs.getString("token", null)

        Log.d("TOKEN_DEBUG", "Loaded token = $authToken")

        buildRetrofit()
    }

    fun setAuthToken(token: String?) {
        authToken = token

        Log.d("TOKEN_DEBUG", "Updated token = $authToken")

        buildRetrofit()
    }

    private fun buildRetrofit() {

        val authInterceptor = Interceptor { chain ->

            val builder = chain.request().newBuilder()

            // ✅ attach token ONLY if present
            authToken?.let {
                builder.addHeader("Authorization", "Bearer $it")
            }

            builder.addHeader("Accept", "application/json")
            builder.addHeader("Content-Type", "application/json")

            Log.d("TOKEN_DEBUG", "Sending token = $authToken")

            chain.proceed(builder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
    }

    val instance: ApiService
        get() {
            if (retrofit == null) {
                throw IllegalStateException("Retrofit not initialized")
            }
            return retrofit!!.create(ApiService::class.java)
        }
}