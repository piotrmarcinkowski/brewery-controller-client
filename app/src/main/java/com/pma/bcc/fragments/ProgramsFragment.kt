package com.pma.bcc.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.pma.bcc.R
import com.pma.bcc.adapters.ProgramsRecyclerViewAdapter
import com.pma.bcc.adapters.ProgramsRecyclerViewAdapter.ItemClickListener
import com.pma.bcc.model.Program
import com.pma.bcc.view.ConnectionErrorView
import com.pma.bcc.view.Navigation
import com.pma.bcc.viewmodels.ProgramsViewModel
import com.pma.bcc.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_programs.view.*
import javax.inject.Inject


class ProgramsFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ProgramsViewModel
    private lateinit var programsRecyclerViewAdapter: ProgramsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_programs, container, false)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgramsViewModel::class.java)

        initProgramsRecyclerView(view)
        initProgramsLoadObservers(view)
        initNavigationEventsObserver()
        observePrograms()
        observeProgramsStates()
        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }

    override fun onPause() {
        viewModel.pause()
        super.onPause()
    }

    private fun initProgramsRecyclerView(view: View) {
        programsRecyclerViewAdapter = ProgramsRecyclerViewAdapter(object : ItemClickListener {
            override fun onItemClick(program: Program) {
                viewModel.showProgramDetails(program)
            }
        })
        (view.programs_recycler_view.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        view.programs_recycler_view.itemAnimator?.changeDuration = 0
        view.programs_recycler_view.adapter = programsRecyclerViewAdapter
    }

    private fun initProgramsLoadObservers(view: View) {
        viewModel.programsLoadInProgress().observe(viewLifecycleOwner, Observer {
            if (it) view.progress_loading.visibility = View.VISIBLE
            else view.progress_loading.visibility = View.GONE
        })
        viewModel.programsLoadError().observe(viewLifecycleOwner, Observer {
            val viewConnectionError = view.view_connection_error
            viewConnectionError.setConnectionError(it)
            viewConnectionError.buttonClickListener = object: ConnectionErrorView.ButtonsClickListener {
                override fun onRetry() {
                    viewModel.retry()
                }

                override fun onExtraAction() {
                    viewModel.extraAction()
                }
            }
        })
    }

    private fun observePrograms() {
        viewModel.getPrograms()
            .observe(viewLifecycleOwner, Observer { programsRecyclerViewAdapter.setPrograms(it) })
    }

    private fun initNavigationEventsObserver() {
        viewModel.navigationEvents().observe(viewLifecycleOwner, Observer {
            Navigation.navigateTo(findNavController(), it)
        })
    }

    private fun observeProgramsStates() {
        viewModel.getProgramStates().observe(
            viewLifecycleOwner,
            Observer { programsRecyclerViewAdapter.setProgramStates(it) })
    }
}
