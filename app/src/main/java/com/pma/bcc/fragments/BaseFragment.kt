package com.pma.bcc.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pma.bcc.view.Navigation
import com.pma.bcc.viewmodels.BaseViewModel
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun onViewModelReady(viewModel: BaseViewModel) {
        initNavigationEventsObserver(viewModel)
        initNotificationEventsObserver(viewModel)
    }

    private fun initNotificationEventsObserver(viewModel: BaseViewModel) {
        viewModel.notificationEvents().observe(viewLifecycleOwner, Observer {
            Toast.makeText(this.context, it.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun initNavigationEventsObserver(viewModel: BaseViewModel) {
        viewModel.navigationEvents().observe(viewLifecycleOwner, Observer {
            Navigation.navigateTo(findNavController(), it)
        })
    }
}
