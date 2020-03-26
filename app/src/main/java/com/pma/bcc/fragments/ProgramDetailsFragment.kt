package com.pma.bcc.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pma.bcc.R
import com.pma.bcc.databinding.FragmentProgramDetailsBinding
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.model.TargetArgumentKey
import com.pma.bcc.view.Navigation
import com.pma.bcc.view.ProgramView
import com.pma.bcc.viewmodels.ProgramDataViewModel
import com.pma.bcc.viewmodels.ProgramDetailsViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.program_grid_item.view.*
import javax.inject.Inject

class ProgramDetailsFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProgramDetailsViewModel
    private lateinit var programView: ProgramView
    private lateinit var program: Program
    private var programState: ProgramState? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentProgramDetailsBinding>(layoutInflater, R.layout.fragment_program_details, container, false)

        programView = binding.root.view_program

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgramDetailsViewModel::class.java)
        viewModel.setProgram(arguments?.getSerializable(TargetArgumentKey.ProgramDetailsProgram.name) as Program)
        viewModel.getProgram().observe(viewLifecycleOwner, Observer { program -> onProgramChanged(program)})
        viewModel.getProgramState().observe(viewLifecycleOwner, Observer { programState -> onProgramStateChanged(programState)})

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initNavigationEventsObserver()

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

    private fun onProgramChanged(program: Program) {
        this.program = program
        updateProgramView()
    }

    private fun onProgramStateChanged(programState: ProgramState) {
        this.programState = programState
        updateProgramView()
    }

    private fun updateProgramView() {
        programView.programDataViewModel = ProgramDataViewModel(program, programState)
    }

    private fun initNavigationEventsObserver() {
        viewModel.navigationEvents().observe(viewLifecycleOwner, Observer {
            Navigation.navigateTo(findNavController(), it)
        })
    }
}
