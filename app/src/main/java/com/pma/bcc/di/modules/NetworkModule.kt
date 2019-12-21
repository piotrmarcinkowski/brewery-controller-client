package com.pma.bcc.di.modules

import com.pma.bcc.model.AppProperties
import dagger.Module
import dagger.Provides
import com.pma.bcc.net.ServerApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    @Provides
    fun provideRetrofit(appProperties : AppProperties) : Retrofit {
        val baseUrl = "${appProperties.serverAddress}:${appProperties.serverPort}${appProperties.apiBaseUrl}"
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