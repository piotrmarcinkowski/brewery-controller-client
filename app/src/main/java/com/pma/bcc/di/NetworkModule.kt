package com.pma.bcc.di

import dagger.Module
import dagger.Provides
import com.pma.bcc.server.ServerApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule(baseUrl: String) {

    private val baseUrl: String = baseUrl

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun provideBCApi(retrofit: Retrofit) : ServerApi {
        return retrofit.create(ServerApi::class.java)
    }
}