package com.example.amazingtalkcalendar.di

import com.example.amazingtalkcalendar.BuildConfig
import com.example.amazingtalkcalendar.network.Service
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

  @Singleton
  @Provides
  fun provideService(retrofit: Retrofit): Service = retrofit.create(Service::class.java)

  @Singleton
  @Provides
  fun provideRetrofit(hostUrl: String, gson: Gson): Retrofit {
    val logging = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
      logging.level = HttpLoggingInterceptor.Level.BODY
    } else {
      logging.level = HttpLoggingInterceptor.Level.NONE
    }
    val client = OkHttpClient().newBuilder().addInterceptor(logging).build()
    return Retrofit.Builder()
      .client(client)
      .baseUrl(hostUrl)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  @Singleton
  @Provides
  fun provideGson(): Gson {
    return GsonBuilder().create()
  }

  @Provides
  fun provideHostUrl(): String = "https://tw.amazingtalker.com/v1/guest/"
}
