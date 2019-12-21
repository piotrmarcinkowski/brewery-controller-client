package com.pma.bcc.di.modules

import androidx.lifecycle.ViewModel
import com.pma.bcc.di.ViewModelKey
import com.pma.bcc.fragments.ProgramDetailsFragment
import com.pma.bcc.fragments.ProgramsFragment
import com.pma.bcc.fragments.SettingsFragment
import com.pma.bcc.viewmodels.ProgramDetailsViewModel
import com.pma.bcc.viewmodels.ProgramsViewModel
import com.pma.bcc.viewmodels.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector(
        modules = [ProgramsViewModelsModule::class]
    )
    abstract fun contributeProgramsFragment(): ProgramsFragment

    @ContributesAndroidInjector(
        modules = [ProgramsDetailsViewModelsModule::class]
    )
    abstract fun contributeProgramDetailsFragment(): ProgramDetailsFragment

    @ContributesAndroidInjector(
        modules = [SettingsViewModelsModule::class]
    )
    abstract fun contributeSettingsFragment(): SettingsFragment
}

@Module
abstract class SettingsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}

@Module
abstract class ProgramsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProgramsViewModel::class)
    abstract fun bindProgramsViewModel(viewModel: ProgramsViewModel): ViewModel
}

@Module
abstract class ProgramsDetailsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProgramDetailsViewModel::class)
    abstract fun bindProgramDetailsViewModel(viewModel: ProgramDetailsViewModel): ViewModel
}