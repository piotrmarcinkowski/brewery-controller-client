package com.pma.bcc

import android.app.Application
import com.pma.bcc.di.ApplicationComponent
import com.pma.bcc.di.DaggerApplicationComponent
import com.pma.bcc.di.NetworkModule

class ClientApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .networkModule(NetworkModule("http://192.168.1.6:8080/brewery/api/v1.0/"))
            .build()
    }
}