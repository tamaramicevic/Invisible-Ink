package com.invisibleink.injection.modules

import com.invisibleink.utils.gson.createGson
import com.invisibleink.utils.interceptors.FetchNotesInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
object NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://162.246.157.171:3000/")
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}