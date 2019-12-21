package com.pma.bcc.di.modules

import androidx.lifecycle.ViewModelProvider
import com.pma.bcc.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory?): ViewModelProvider.Factory?
}