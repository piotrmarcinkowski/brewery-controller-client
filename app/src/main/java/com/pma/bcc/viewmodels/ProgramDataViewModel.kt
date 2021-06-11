package com.pma.bcc.viewmodels

import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState

const val TEMP_ZERO_STR = "0.0"

class ProgramDataViewModel(val program: Program, val state: ProgramState?) {

    fun getTemperatureReadInProgress(): Boolean = state == null

    fun currentTemperature(): String =
        state?.currentTemp?.displayString() ?: TEMP_ZERO_STR

    fun getName(): String = program.name

    fun getHeatingActivated(): Boolean = state != null && state.heatingActivated

    fun getCoolingActivated(): Boolean = state != null && state.coolingActivated

    fun getMaxTemperature(): String = program.maxTemp.displayString()

    fun getMinTemperature(): String = program.minTemp.displayString()

    fun getActive(): Boolean = program.active
}
