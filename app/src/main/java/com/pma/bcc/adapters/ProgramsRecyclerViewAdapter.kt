package com.pma.bcc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pma.bcc.R
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.utils.TemperatureFormatter
import com.pma.bcc.view.ProgramView
import kotlinx.android.synthetic.main.program_grid_item.view.*
import java.util.*

class ProgramsRecyclerViewAdapter(
    private val itemClickListener : ItemClickListener

) : RecyclerView.Adapter<ViewHolder>() {

    private var programs : List<Program> = Collections.emptyList()
    private var programStates : Map<String, ProgramState> = Collections.emptyMap()

    interface ItemClickListener {
        fun onItemClick(program : Program)
    }

    fun setPrograms(programs: List<Program>) {
        this.programs = programs
        notifyDataSetChanged()
    }

    fun setProgramStates(programStates: Map<String, ProgramState>) {
        this.programStates = programStates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_grid_item, parent, false))
    }

    override fun getItemCount(): Int {
        return programs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]
        holder.view.setOnClickListener { itemClickListener.onItemClick(program) }
        holder.programView.programName = program.name
        holder.programView.maxTemperature = TemperatureFormatter.format(program.maxTemp)
        holder.programView.minTemperature = TemperatureFormatter.format(program.minTemp)
        holder.programView.active = program.active
        val state = programStates[program.id]

        holder.programView.currentTemperatureAvailable = state != null
        holder.programView.currentTemperature = if (state != null) TemperatureFormatter.format(state.currentTemp) else ""
        holder.programView.heatingActivated = state?.heatingActivated ?: false
        holder.programView.coolingActivated = state?.coolingActivated ?: false
    }
}

class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val programView : ProgramView = view.view_program
}