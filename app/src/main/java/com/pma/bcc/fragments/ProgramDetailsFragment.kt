package com.pma.bcc.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pma.bcc.R
import com.pma.bcc.databinding.FragmentProgramDetailsBinding
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.model.TargetArgumentKey
import com.pma.bcc.view.ProgramView
import com.pma.bcc.viewmodels.ProgramDataViewModel
import com.pma.bcc.viewmodels.ProgramDetailsViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.program_grid_item.view.*
import javax.inject.Inject

class ProgramDetailsFragment : BaseFragment() {
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

        onViewModelReady(viewModel)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_program_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_program -> {
                showProgramDeleteConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showProgramDeleteConfirmation() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.confirmation_delete_program_ok) {
                        dialog, id -> viewModel.onClickDeleteProgram()
                }
                setNegativeButton(R.string.confirmation_delete_program_cancel, null)
                setMessage(R.string.confirmation_delete_program_message)
                setTitle(R.string.confirmation_delete_program_title)
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun onProgramChanged(program: Program) {
        this.program = program
        updateProgramView()
    }

    private fun onProgramStateChanged(programState: ProgramState?) {
        this.programState = programState
        updateProgramView()
    }

    private fun updateProgramView() {
        programView.programDataViewModel = ProgramDataViewModel(program, programState)
    }
}
