package com.jonapoka.luftr.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Hardcoded API keys (will be updated later)
    const val GROQ_API_KEY = "gsk_placeholder_key_will_be_updated"
    const val EXERCISEDB_API_KEY = "placeholder_rapidapi_key"
    
    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val groqRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val exerciseDbRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val groqApi: GroqApiService by lazy {
        groqRetrofit.create(GroqApiService::class.java)
    }
    
    val exerciseDbApi: ExerciseDbApiService by lazy {
        exerciseDbRetrofit.create(ExerciseDbApiService::class.java)
    }
}
