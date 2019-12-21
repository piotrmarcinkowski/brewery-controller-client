package com.pma.bcc.viewmodels

import com.pma.bcc.net.ServerApiFactory
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private var serverApiFactory: ServerApiFactory) :
    BaseViewModel() {

    fun onSettingsChanged() {
        serverApiFactory.invalidate()
    }
}