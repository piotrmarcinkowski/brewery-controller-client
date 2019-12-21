package com.pma.bcc

import com.pma.bcc.di.components.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import mu.KLogging


class ClientApplication : DaggerApplication() {
    companion object : KLogging()

    override fun onCreate() {
        super.onCreate()
        logger.info("onCreate")
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().application(this).build()
    }
}