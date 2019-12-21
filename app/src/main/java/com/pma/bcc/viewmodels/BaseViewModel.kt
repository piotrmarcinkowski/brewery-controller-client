package com.pma.bcc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pma.bcc.model.NavigationTarget
import mu.KLogging

open class BaseViewModel : ViewModel() {
    companion object: KLogging()

    private val navigationEvents : SingleLiveEvent<NavigationTarget> = SingleLiveEvent()

    fun navigateTo(target: NavigationTarget) {
        logger.info("Navigating to $target")
        navigationEvents.value = target
    }

    fun navigationEvents() : LiveData<NavigationTarget> {
        return navigationEvents
    }

}