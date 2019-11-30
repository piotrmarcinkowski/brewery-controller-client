package com.pma.bcc.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.pma.bcc.R
import kotlinx.android.synthetic.main.fragment_programs.*

class ProgramsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programs, container, false)
    }

    override fun onResume() {
        super.onResume()
        go_to_settings.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_programsFragment_to_settingsFragment, null))
    }
}
