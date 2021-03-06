package com.quanticheart.repository.retrofit

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quanticheart.repository.BuildConfig
import com.quanticheart.repository.retrofit.interceptor.AuthInterceptor
import com.quanticheart.repository.retrofit.interceptor.CacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://tcg.poketudo.com/api/"
private const val CACHE_SIZE = 5 * 1024 * 1024L // 5 MB de cache

class RetrofitClient(private val application: Context) {

    private val gson: Gson by lazy { GsonBuilder().create() }

    private val okHttp: OkHttpClient by lazy {
        val client = OkHttpClient.Builder()
            .cache(cacheSize())
            .addNetworkInterceptor(CacheInterceptor)
            .addInterceptor(AuthInterceptor())

        val log = HttpLoggingInterceptor()
        when (BuildConfig.DEBUG) {
            true ->
                log.level = HttpLoggingInterceptor.Level.BODY
            false -> log.level = HttpLoggingInterceptor.Level.NONE
        }
        client.addInterceptor(log)

        client.build()
    }

    fun newInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun cacheSize(): Cache {
        return Cache(application.cacheDir, CACHE_SIZE)
    }
}