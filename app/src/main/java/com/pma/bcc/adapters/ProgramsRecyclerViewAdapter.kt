package com.pma.bcc.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pma.bcc.R
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.view.ProgramView
import com.pma.bcc.viewmodels.ProgramDataViewModel
import kotlinx.android.synthetic.main.program_grid_item.view.*
import java.util.*

class ProgramsRecyclerViewAdapter : RecyclerView.Adapter<ViewHolder> {

    constructor(itemClickListener : ItemClickListener) {
        this.itemClickListener = itemClickListener
        setHasStableIds(true)
    }

    private val itemClickListener: ItemClickListener
    private var programs : List<Program> = Collections.emptyList()
    private var programStates : Map<String, ProgramState> = Collections.emptyMap()

    interface ItemClickListener {
        fun onItemClick(program : Program)
    }

    fun setPrograms(programs: List<Program>) {
        Log.d("Adapter", "setPrograms: $programs")
        this.programs = programs
        notifyDataSetChanged()
    }

    fun setProgramStates(programStates: Map<String, ProgramState>) {
        Log.d("Adapter", "setProgramStates: $programStates")
        this.programStates = programStates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_grid_item, parent, false))
    }

    override fun getItemCount(): Int {
        return programs.size
    }

    override fun getItemId(position: Int): Long {
        return programs[position].id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]
        val state = programStates[program.id]

        holder.programView.programDataViewModel = ProgramDataViewModel(program, state)
        holder.view.setOnClickListener { itemClickListener.onItemClick(program) }
    }
}

class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val programView : ProgramView = view.view_program
}
