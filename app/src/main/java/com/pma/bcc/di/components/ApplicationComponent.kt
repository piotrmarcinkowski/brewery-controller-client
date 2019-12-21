package com.pma.bcc.di.components

import android.app.Application
import com.pma.bcc.ClientApplication
import com.pma.bcc.di.modules.FragmentBuildersModule
import com.pma.bcc.di.modules.ApplicationModule
import com.pma.bcc.di.modules.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    FragmentBuildersModule::class,
    ApplicationModule::class,
    ViewModelFactoryModule::class])
interface ApplicationComponent : AndroidInjector<ClientApplication> {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application : Application) : Builder

        fun build() : ApplicationComponent
    }


}