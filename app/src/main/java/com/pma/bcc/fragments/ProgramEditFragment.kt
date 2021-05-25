package com.pma.bcc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.pma.bcc.R
import com.pma.bcc.databinding.FragmentProgramEditBinding
import com.pma.bcc.model.Program
import com.pma.bcc.model.TargetArgumentKey
import com.pma.bcc.view.Navigation
import com.pma.bcc.viewmodels.ProgramEditViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import javax.inject.Inject
import dagger.android.support.DaggerFragment

class ProgramEditFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProgramEditViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentProgramEditBinding>(
            layoutInflater,
            R.layout.fragment_program_edit,
            container,
            false
        )

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProgramEditViewModel::class.java)
        if (arguments?.getSerializable(TargetArgumentKey.ProgramEditProgram.name) != null) {
            viewModel.setProgram(arguments?.getSerializable(TargetArgumentKey.ProgramEditProgram.name) as Program)
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        onViewModelReady(viewModel)

        return binding.root
    }
}
