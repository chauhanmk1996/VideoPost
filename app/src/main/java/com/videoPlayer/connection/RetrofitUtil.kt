package com.videoPlayer.connection

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

interface RetrofitUtil {
    companion object {
        fun create(
            baseUrl: String,
            connectTimeoutInSec: Long = 60,
            readTimeoutInSec: Long = 60,
            writeTimeoutInSec: Long = 60
        ): ApiServices {
            val client = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
            client.connectTimeout(connectTimeoutInSec, TimeUnit.SECONDS)
            client.readTimeout(readTimeoutInSec, TimeUnit.SECONDS)
            client.writeTimeout(writeTimeoutInSec, TimeUnit.SECONDS)
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client.build())
                .baseUrl(baseUrl)
                .build()
            return retrofit.create(ApiServices::class.java)
        }
    }
}