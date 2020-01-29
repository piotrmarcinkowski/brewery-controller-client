package com.pma.bcc.viewmodels

import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.utils.TemperatureFormatter

const val TEMP_ZERO_STR = "0.0"

class ProgramDataViewModel(val program: Program, val state: ProgramState?) {

    fun getTemperatureReadInProgress(): Boolean = state == null

    fun currentTemperature(): String =
        if (state != null) TemperatureFormatter.format(state.currentTemp)
        else TEMP_ZERO_STR

    fun getName(): String = program.name

    fun getHeatingActivated(): Boolean = state != null && state.heatingActivated

    fun getCoolingActivated(): Boolean = state != null && state.coolingActivated

    fun getMaxTemperature(): String = TemperatureFormatter.format(program.maxTemp)

    fun getMinTemperature(): String = TemperatureFormatter.format(program.minTemp)

    fun getActive(): Boolean = program.active
}
