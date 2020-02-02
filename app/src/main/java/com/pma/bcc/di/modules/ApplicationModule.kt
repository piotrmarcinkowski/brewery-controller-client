package com.pma.bcc.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pma.bcc.androidglue.AndroidAppProperties
import com.pma.bcc.androidglue.AndroidResourceProvider
import com.pma.bcc.model.AppProperties
import com.pma.bcc.model.ProgramsRepository
import com.pma.bcc.model.ProgramsRepositoryImpl
import com.pma.bcc.net.FakeServerApiFactoryImpl
import com.pma.bcc.net.ServerApiFactory
import com.pma.bcc.viewmodels.ResourceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideContext(application : Application) : Context {
        return application
    }

    @Provides
    @Singleton
    fun provideResourceProvider(context: Context) : ResourceProvider {
        return AndroidResourceProvider(context)
    }

    @Provides
    @Singleton
    fun provideAppProperties(
        context: Context,
        sharedPreferences: SharedPreferences
    ): AppProperties {
        return AndroidAppProperties(context, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRetrofitFactory(appProperties: AppProperties): ServerApiFactory {
        //return ServerApiFactoryImpl(appProperties)
        return FakeServerApiFactoryImpl(appProperties)
    }

    @Provides
    @Singleton
    fun provideProgramsRepository(serverApiFactory: ServerApiFactory): ProgramsRepository {
        return ProgramsRepositoryImpl(serverApiFactory)
    }
}