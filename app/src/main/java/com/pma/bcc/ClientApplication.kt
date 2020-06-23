package com.pma.bcc

import android.os.Build
import com.pma.bcc.di.components.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import mu.KLogging
import org.slf4j.impl.HandroidLoggerAdapter


class ClientApplication : DaggerApplication() {
    companion object : KLogging()

    override fun onCreate() {
        super.onCreate()

        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG;
        HandroidLoggerAdapter.ANDROID_API_LEVEL = Build.VERSION.SDK_INT;
        HandroidLoggerAdapter.APP_NAME = "bcc";

        logger.info("onCreate")
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().application(this).build()
    }
}