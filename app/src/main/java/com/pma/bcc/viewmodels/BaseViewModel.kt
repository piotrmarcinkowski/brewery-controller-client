package com.pma.bcc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pma.bcc.model.NavigationTarget
import com.pma.bcc.model.TargetId
import mu.KLogging

data class Notification(val message: String)

open class BaseViewModel : ViewModel() {
    companion object: KLogging()

    private val navigationEvents = SingleLiveEvent<NavigationTarget>()
    private val notificationEvents = SingleLiveEvent<Notification>()

    fun navigateTo(target: NavigationTarget) {
        logger.info("Navigating to $target")
        navigationEvents.value = target
    }

    fun navigateBack() {
        logger.info("Navigating back")
        navigationEvents.value = NavigationTarget(TargetId.Back)
    }

    fun navigationEvents() : LiveData<NavigationTarget> {
        return navigationEvents
    }

    fun notificationEvents() : LiveData<Notification> {
        return notificationEvents
    }

    fun showNotification(notification: Notification) {
        notificationEvents.value = notification
    }

}