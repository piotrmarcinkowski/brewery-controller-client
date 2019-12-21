package com.pma.bcc.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.pma.bcc.R
import com.pma.bcc.databinding.FragmentProgramDetailsBinding
import com.pma.bcc.model.Program
import com.pma.bcc.model.TargetArgumentKey
import com.pma.bcc.viewmodels.ProgramDetailsViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ProgramDetailsFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProgramDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentProgramDetailsBinding>(layoutInflater, R.layout.fragment_program_details, container, false)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgramDetailsViewModel::class.java)
        viewModel.setProgram(arguments?.getSerializable(TargetArgumentKey.ProgramDetailsProgram.name) as Program)

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }

    override fun onPause() {
        viewModel.pause()
        super.onPause()
    }
}
