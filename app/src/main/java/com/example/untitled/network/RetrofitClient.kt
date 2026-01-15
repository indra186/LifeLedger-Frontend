package com.example.untitled.network

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2/lifeledger/"

    private var authToken: String? = null
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        authToken = prefs.getString("token", null)
        buildRetrofit()
    }

    fun setAuthToken(token: String?) {
        authToken = token
        buildRetrofit()   // REBUILD when token changes
    }

    private fun buildRetrofit() {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${authToken ?: ""}")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    val instance: ApiService
        get() {
            if (retrofit == null) {
                buildRetrofit()
            }
            return retrofit!!.create(ApiService::class.java)
        }
}
