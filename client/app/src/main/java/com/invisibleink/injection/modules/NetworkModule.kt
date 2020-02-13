package com.invisibleink.injection.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://TODO")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}