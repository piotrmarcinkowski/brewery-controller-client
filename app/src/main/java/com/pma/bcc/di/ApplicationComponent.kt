package com.pma.bcc.di

import dagger.Component
import com.pma.bcc.MainActivity
import javax.inject.Singleton

@Component (modules = [NetworkModule::class])
@Singleton
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)
}