package com.gym.gymapp.network

import com.gym.gymapp.utils.BASE_URL
import com.gym.gymapp.utils.DISTANCE_MATRIX
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstanceDistanceMatrix {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().baseUrl(DISTANCE_MATRIX).addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        }
        val api: GymApi by lazy {
            retrofit.create(GymApi::class.java)
        }
    }
}
